package com.spicy.backend.settlement.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record SettlementItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {}