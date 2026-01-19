package com.spicy.backend.order.application;

import com.spicy.backend.global.error.errorcode.GlobalErrorCode;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.ProductRepository;
import com.spicy.backend.order.dao.cartitems.CartItemRepository;
import com.spicy.backend.order.domain.CartItem;
import com.spicy.backend.order.domain.Product;
import com.spicy.backend.order.dto.request.CartItemCreateRequest;
import com.spicy.backend.order.dto.response.CartItemResponse;
import com.spicy.backend.order.error.CartItemErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTests {
    @InjectMocks
    private CartItemService cartItemService;

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;

    private Long userId;
    private Long storeId;
    private Long productId;
    private Long quantity;
    private Long cartId;

    private CartItem cartItem;
    private Product product;

    CartItemCreateRequest cartItemCreateRequest;

    @BeforeEach
    void setUp() {
        userId = 1L;
        storeId = 10L;
        productId = 100L;

        quantity = 20L;

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
        ReflectionTestUtils.setField(cartItem, "id", cartId);

        cartItemCreateRequest = new CartItemCreateRequest(
                productId,
                quantity
        );
    }

    @Test
    @DisplayName("장바구니 상품 생성 - 성공")
    void addCartItem_Success() {
        // given
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        given(cartItemRepository.saveAll(anyList())).willReturn(List.of(cartItem));

        // when
        List<Long> response = cartItemService.addCartItem(userId, storeId, List.of(cartItemCreateRequest));

        // then
        assertEquals(cartId, response.get(0));
    }
    @Test
    @DisplayName("장바구니 상품 생성 - 실패 - RESOURCE_NOT_FOUND")
    void addCartItem_Failure_RESOURCE_NOT_FOUND() {
        // given
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                cartItemService.addCartItem(userId, storeId, List.of(cartItemCreateRequest))
        );
        assertEquals(GlobalErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("장바구니 상품 조회 - 성공")
    void getCartItems_Success() {
        // given
        given(cartItemRepository.findAllByUserIdAndStoreIdAndDeletedAtIsNull(userId, storeId)).willReturn(List.of(cartItem));

        // when
        List<CartItemResponse> responses = cartItemService.getCartItems(userId, storeId);

        // then
        assertEquals(product, responses.get(0).product());
        assertEquals(quantity, responses.get(0).quantity());
    }


    @Test
    @DisplayName("장바구니 상품 삭제 - 성공")
    void deleteCartItem_Success() {
        // given
        given(cartItemRepository.findByUserIdAndIdAndStoreIdAndDeletedAtIsNull(userId, cartItem.getId(), storeId)).willReturn(Optional.of(cartItem));

        // when & then
        cartItemService.deleteCartItem(userId, cartItem.getId(), storeId);

        verify(cartItemRepository, times(1)).delete(cartItem);
    }
    @Test
    @DisplayName("장바구니 상품 삭제 - 실패 - CART_NOT_FOUND")
    void deleteCartItem_Failure_CART_NOT_FOUND() {
        // given
        given(cartItemRepository.findByUserIdAndIdAndStoreIdAndDeletedAtIsNull(userId, cartItem.getId(), storeId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                cartItemService.deleteCartItem(userId, cartItem.getId(), storeId)
        );
        assertEquals(CartItemErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }
}
