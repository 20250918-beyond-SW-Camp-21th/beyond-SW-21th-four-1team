package com.spicy.backend.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemRequest(
        // 상품 식별 번호
        @NotNull
        Long productId,

        // 상품 이름
        @NotBlank
        String productName,

        // 주문 수량
        @NotNull
        @Min(1)
        Long quantity,

        // 개별 단가
        @NotNull
        @Min(1)
        BigDecimal unitPrice
) {
}
