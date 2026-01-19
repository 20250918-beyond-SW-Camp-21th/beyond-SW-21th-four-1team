package com.spicy.backend.order.application;

import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.cartitems.CartItemRepository;
import com.spicy.backend.order.dao.order.OrderItemRepository;
import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.order.domain.CartItem;
import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.domain.OrderItem;
import com.spicy.backend.order.domain.Product;
import com.spicy.backend.order.dto.request.OrderCreateRequest;
import com.spicy.backend.order.dto.request.OrderItemRequest;
import com.spicy.backend.order.dto.request.wrapper.OrderAndOrderItemRequest;
import com.spicy.backend.order.dto.response.OrderCanceledResponse;
import com.spicy.backend.order.dto.response.OrderCreateResponse;
import com.spicy.backend.order.dto.response.OrderItemResponse;
import com.spicy.backend.order.dto.response.OrderResponse;
import com.spicy.backend.order.enums.Status;
import com.spicy.backend.order.error.OrderErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
        @InjectMocks
        private OrderService orderService;

        @Mock
        private OrderRepository orderRepository;
        @Mock
        private OrderItemRepository orderItemRepository;
        @Mock
        private CartItemRepository cartItemRepository;

        private Long userId;
        private Long orderId;
        private Long storeId;
        private Long productId;
        private Long orderItemId;

        private Long quantity;

        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        private Order order;
        private OrderAndOrderItemRequest orderAndOrderItemRequest;
        private OrderCreateRequest orderCreateRequest;
        private OrderItemRequest orderItemRequest;
        private List<OrderItemRequest> orderItemRequestList;
        private List<Order> orderList;

        private OrderItem orderItem;

        private CartItem cartItem;

        private Product product;

        @BeforeEach
        void setUp() {
                userId = 1L;
                orderId = 10L;
                storeId = 100L;
                productId = 1000L;
                orderItemId = 20L;

                quantity = 10L;

                unitPrice = BigDecimal.valueOf(100.00);
                totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

                order = Order.builder()
                                .orderNumber("orderNumber")
                                .storeId(storeId)
                                .totalAmount(BigDecimal.valueOf(0))
                                .status(Status.PENDING)
                                .address("address")
                                .receiverPhone("receiverPhone")
                                .receiverName("receiverName")
                                .memo("memo")
                                .build();
                ReflectionTestUtils.setField(order, "id", orderId);

                orderList = List.of(order);

                orderItem = OrderItem.builder()
                                .orderId(orderId)
                                .productName("productName")
                                .productId(productId)
                                .quantity(quantity)
                                .unitPrice(unitPrice)
                                .totalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                                .build();
                ReflectionTestUtils.setField(orderItem, "id", orderItemId);

                product = Product.builder()
                                .price(BigDecimal.valueOf(100.00))
                                .build();
                ReflectionTestUtils.setField(product, "productId", productId);

                cartItem = CartItem.builder()
                                .storeId(storeId)
                                .userId(userId)
                                .product(product)
                                .quantity(quantity)
                                .build();
        }

        @Test
        @DisplayName("주문 생성 - 성공")
        public void createOrder_Success() {
                // given
                OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
                                LocalDate.now(),
                                "address",
                                "receiverName",
                                "receiverPhone",
                                "memo");

                List<OrderItemRequest> orderItemRequestList = List.of(
                                new OrderItemRequest(
                                                productId,
                                                "productName",
                                                quantity,
                                                unitPrice));

                Order mockOrder = Order.builder()
                                .orderNumber("orderNumber")
                                .storeId(storeId)
                                .totalAmount(BigDecimal.valueOf(0))
                                .status(Status.PENDING)
                                .address("address")
                                .receiverPhone("receiverPhone")
                                .receiverName("receiverName")
                                .memo("memo")
                                .build();
                ReflectionTestUtils.setField(mockOrder, "id", orderId);
                given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

                given(orderItemRepository.saveAll(anyList())).willReturn(List.of());

                given(cartItemRepository.findAllByUserIdAndStoreIdAndDeletedAtIsNull(userId, storeId))
                                .willReturn(List.of(cartItem));

                // when
                OrderCreateResponse response = orderService.createOrder(storeId, userId, orderCreateRequest);

                // then
                assertEquals(orderId, response.orderId());
        }

         @Test
         @DisplayName("주문 전체 조회 - 성공")
         void getOrders_Success() {
         // given
         given(orderRepository.findAllByUserIdAndStoreIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(userId, storeId, Status.PENDING)).willReturn(orderList);

         // when
         List<OrderResponse> response = orderService.getAllOrders(userId, storeId, Status.PENDING);

         // then
         assertEquals(orderId, response.getFirst().orderId());

         verify(orderRepository, times(1)).findAllByUserIdAndStoreIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(userId, storeId, Status.PENDING);
         }

        @Test
        @DisplayName("주문 정보 상세 조회 - 성공")
        void getOrderDetails_Success() {
                // given
                given(orderItemRepository.findAllByUserIdAndStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                                userId, storeId, orderId)).willReturn(List.of(orderItem));

                // when
                List<OrderItemResponse> response = orderService.getOrderDetails(userId, storeId, orderId);

                // then
                assertEquals("productName", response.get(0).productName());
                assertEquals(quantity, response.get(0).quantity());
                assertEquals(unitPrice, response.get(0).unitPrice());
                assertEquals(totalPrice, response.get(0).totalPrice());
        }

        @Test
        @DisplayName("주문 정보 상세 조회 - 실패 - ORDER_ITEM_NOT_FOUND")
        void getOrderDetails_Failure_ORDER_ITEM_NOT_FOUND() {
                // given
                given(orderItemRepository.findAllByUserIdAndStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                                userId, storeId, orderId)).willReturn(List.of());

                // when & then
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.getOrderDetails(userId, storeId, orderId));
                assertEquals("주문 상품이 존재하지 않거나 권한이 없습니다.", exception.getMessage());
                assertEquals(OrderErrorCode.ORDER_ITEM_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("주문 취소 - 성공")
        void cancelOrder_Success() {
                // given
                given(orderRepository.findByUserIdAndStoreIdAndIdAndDeletedAtIsNull(userId, storeId, orderId))
                                .willReturn(Optional.of(order));

                given(orderItemRepository.findAllByUserIdAndStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                                userId, storeId, orderId)).willReturn(List.of(orderItem));

                // when
                OrderCanceledResponse response = orderService.cancelOrder(userId, storeId, orderId);

                // then
                assertEquals(Status.CANCELLED, response.order().status());
                assertThat(orderItem)
                                .matches(item -> item.getStatus().equals(Status.CANCELLED));
        }

        @Test
        @DisplayName("주문 실패 - ORDER_NOT_FOUND")
        void cancelOrder_Failure_ORDER_NOT_FOUND() {
                // given
                given(orderRepository.findByUserIdAndStoreIdAndIdAndDeletedAtIsNull(userId, storeId, orderId))
                                .willReturn(Optional.empty());

                // then & when
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.cancelOrder(userId, storeId, orderId));
                assertEquals(OrderErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        }
}
