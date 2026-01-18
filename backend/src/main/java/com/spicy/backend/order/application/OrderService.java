package com.spicy.backend.order.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.cartitems.CartItemRepository;
import com.spicy.backend.order.dao.order.OrderItemRepository;
import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.order.domain.CartItem;
import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.domain.OrderItem;
import com.spicy.backend.order.dto.request.OrderCreateRequest;
import com.spicy.backend.order.dto.response.OrderCanceledResponse;
import com.spicy.backend.order.dto.response.OrderCreateResponse;
import com.spicy.backend.order.dto.response.OrderItemResponse;
import com.spicy.backend.order.dto.response.OrderResponse;
import com.spicy.backend.order.enums.Status;
import com.spicy.backend.order.error.CartItemErrorCode;
import com.spicy.backend.order.error.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderCreateResponse createOrder(Long storeId, Long userId, OrderCreateRequest request) {
        // 사용자 검증 및 장바구니 가져오기
        List<CartItem> cartList = cartItemRepository.findAllByUserIdAndStoreIdAndDeletedAtIsNull(userId, storeId);
        if (cartList.isEmpty()) throw new BusinessException(CartItemErrorCode.CART_ITEM_NOT_FOUND);

        // Order 생성 및 저장
        Order order = orderRepository.save(Order.create(userId, request, storeId));

        // OrderItem 생성 및 저장
        createAndSaveOrderItems(cartList, order);

        // 장바구니에서 삭제
        cartItemRepository.deleteAll(cartList);

        return OrderCreateResponse.from(order.getId());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(Long userId, Long storeId, Status status) {
        // status에 따라 주문 리스트 조회
        List<Order> orders = orderRepository.findAllByUserIdAndStoreIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(userId, storeId, status);

        // 리스트 반환
        return OrderResponse.from(orders);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderDetails(Long userId, Long storeId, Long orderId) {
        // userId, storeId, orderId로 OrderItem 리스트 조회
        // 리스트 길이가 0일 때 예외 발생
        List<OrderItem> itemList = orderItemRepository.findAllByUserIdAndStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, storeId, orderId);
        if (itemList.isEmpty()) throw new BusinessException(OrderErrorCode.ORDER_ITEM_NOT_FOUND);

        // OrderItem 리스트를 OrderItemResponse 리스트로 변환 후 반환
        return OrderItemResponse.from(itemList);
    }

    @Transactional(rollbackFor = BusinessException.class)
    public OrderCanceledResponse cancelOrder(Long userId, Long storeId, Long orderId) {
        // 사용자 검증
        Order order = orderRepository.findByUserIdAndStoreIdAndIdAndDeletedAtIsNull(userId, storeId, orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
        List<OrderItem> items = orderItemRepository.findAllByUserIdAndStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, storeId, orderId);

        // 주문과 주문 상품의 상태를 취소로 변경
        order.updateStatus(Status.CANCELLED);
        for (OrderItem orderItem : items) {
            orderItem.updateStatus(Status.CANCELLED);
        }

        return OrderCanceledResponse.from(order, items);
    }

    private void createAndSaveOrderItems(
            List<CartItem> cartList,
            Order order
    ) {
        // CartItem -> OrderItem 변환
        List<OrderItem> itemList = cartList.stream()
                .map(item -> {
                    OrderItem orderItem = OrderItem.create(item);
                    orderItem.updateOrderId(order.getId());
                    return orderItem;
                })
                .toList();

        // 총 주문 금액 계산
        BigDecimal totalPrice = itemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 총 주문 금액 갱신
        order.updateTotalPrice(totalPrice);

        // OrderItem 저장
        orderItemRepository.saveAll(itemList);
    }
}
