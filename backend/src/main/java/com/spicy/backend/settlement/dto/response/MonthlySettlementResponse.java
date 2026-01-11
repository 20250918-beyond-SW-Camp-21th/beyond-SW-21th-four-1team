package com.spicy.backend.settlement.dto.response;

import com.spicy.backend.settlement.enums.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlySettlementResponse(
        @Schema(description = "해당 월 주문 금액 합계", example = "5000000.00")
        BigDecimal totalAmount,

        @Schema(description = "수수료 금액", example = "5000000.00")
        BigDecimal commissionAmount,

        @Schema(description = "최종 정산 금액", example = "5000000.00")
        BigDecimal settlementAmount,

        @Schema(description = "정산 상태", example = "WAITING")
        SettlementStatus status,

        @Schema(description = "지급 예정일", example = "2026-02-10")
        LocalDate payoutDate



) {
}
