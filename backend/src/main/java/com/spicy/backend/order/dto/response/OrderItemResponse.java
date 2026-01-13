package com.spicy.backend.order.dto.response;

import com.spicy.backend.order.domain.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemResponse(
        String productName,

        Long quantity,

        BigDecimal unitPrice,

        BigDecimal totalPrice
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }

    public static List<OrderItemResponse> from(List<OrderItem> itemList) {
        return itemList.stream()
                .map(OrderItemResponse::from)
                .toList();
    }
}
