package com.spicy.backend.order.dto.request.wrapper;

import com.spicy.backend.order.dto.request.OrderCreateRequest;
import com.spicy.backend.order.dto.request.OrderItemRequest;

import java.util.List;

public record OrderAndOrderItemRequest(
        OrderCreateRequest orderCreateRequest,
        List<OrderItemRequest> orderItemRequestList
) {
}
