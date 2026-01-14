package com.spicy.backend.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemCreateRequest(
                @NotNull Long productId,

                @NotNull @Min(1) Long quantity) {
}
