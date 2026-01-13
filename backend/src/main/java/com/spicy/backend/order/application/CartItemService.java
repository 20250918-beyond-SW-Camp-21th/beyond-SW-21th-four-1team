package com.spicy.backend.order.application;

import com.spicy.backend.global.error.errorcode.GlobalErrorCode;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.ProductRepository;
import com.spicy.backend.order.dao.cartitems.CartItemRepository;
import com.spicy.backend.order.domain.CartItem;
import com.spicy.backend.order.dto.request.CartItemCreateRequest;
import com.spicy.backend.order.dto.response.CartItemResponse;
import com.spicy.backend.order.error.CartItemErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(rollbackFor = BusinessException.class)
    public List<Long> addCartItem(Long userId, Long storeId, List<CartItemCreateRequest> requests) {
        // CartItem 생성 및 저장
        List<CartItem> cartItems = new ArrayList<>();

        createCartItems(cartItems, requests, userId, storeId);

        List<CartItem> savedCartItems = cartItemRepository.saveAll(cartItems);

        return savedCartItems.stream()
                .map(CartItem::getId)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(Long userId, Long storeId) {
        // CartItem 조회
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdAndStoreIdAndDeletedAtIsNull(userId, storeId);

        return CartItemResponse.from(cartItems);
    }

    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId, Long storeId) {
        // 사용자 검증
        CartItem item = cartItemRepository.findByUserIdAndIdAndStoreIdAndDeletedAtIsNull(userId, cartItemId, storeId)
                .orElseThrow(() -> new BusinessException(CartItemErrorCode.CART_ITEM_NOT_FOUND));

        // 장바구니 상품 삭제
        cartItemRepository.delete(item);
    }

    private void createCartItems(
            List<CartItem> cartItemList,
            List<CartItemCreateRequest> requests,
            Long userId,
            Long storeId
    ) {
        for (CartItemCreateRequest request : requests) {
            CartItem cartItem = CartItem.builder()
                    .storeId(storeId)
                    .userId(userId)
                    .product(
                            productRepository.findById(request.productId())
                                    .orElseThrow(() -> new BusinessException(GlobalErrorCode.RESOURCE_NOT_FOUND))
                            // 나중에 에러 코드 변경해야함 PRODUCT_NOT_FOUND
                    )
                    .quantity(request.quantity())
                    .build();
            cartItemList.add(cartItem);
        }
    }
}
