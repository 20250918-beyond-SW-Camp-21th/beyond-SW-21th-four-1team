package com.spicy.backend.demandplan.controller.dto.response;

import java.time.LocalDate;

public record SettlementResponseDto(
        Long productId,
        LocalDate settlementDate,
        Integer orderCount        // 주문량
) {
}
