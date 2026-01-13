package com.spicy.backend.order.dto.response;

import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.domain.OrderItem;

import java.util.List;

public record OrderCanceledResponse(
        OrderResponse order,
        List<OrderItemResponse> items
) {
    public static OrderCanceledResponse from(Order order, List<OrderItem> items) {
        OrderResponse orderResponse = OrderResponse.from(order);
        List<OrderItemResponse> itemResponses = OrderItemResponse.from(items);

        return new OrderCanceledResponse(orderResponse, itemResponses);
    }
}
