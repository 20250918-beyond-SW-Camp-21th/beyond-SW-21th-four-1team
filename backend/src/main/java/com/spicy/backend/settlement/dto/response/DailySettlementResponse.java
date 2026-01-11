package com.spicy.backend.settlement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record DailySettlementResponse(
        @Schema(description = "주문 건수", example = "15")
        Integer orderCount,

        @Schema(description = "일 주문 금액 합계", example = "150000.00")
        BigDecimal dailyAmount,

        @Schema(description = "해당 월 누적 주문 금액", example = "4500000.00")
        BigDecimal monthlyAccumulatedAmount
) {}
