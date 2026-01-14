package com.spicy.backend.order.dto.response;

import com.spicy.backend.order.domain.CartItem;
import com.spicy.backend.order.domain.Product;

import java.util.List;

public record CartItemResponse(
        Long cartItemId,
        Product product,
        Long quantity) {
    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct(),
                cartItem.getQuantity());
    }

    public static List<CartItemResponse> from(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItemResponse::from)
                .toList();
    }
}
