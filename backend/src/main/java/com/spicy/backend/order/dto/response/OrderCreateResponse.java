package com.spicy.backend.order.dto.response;

import jakarta.validation.constraints.NotNull;

public record OrderCreateResponse(
        @NotNull
        Long orderId
) {
    public static OrderCreateResponse from(Long id) {
        return new OrderCreateResponse(id);
    }
}
