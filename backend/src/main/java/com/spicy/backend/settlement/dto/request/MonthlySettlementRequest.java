package com.spicy.backend.settlement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record MonthlySettlementRequest(
        @NotNull
        @Schema(description = "가맹점 식별 번호", example = "1")
        Long storeId,

        @NotNull
        @PastOrPresent(message = "미래 날짜의 정산은 생성하거나 조회할 수 없습니다.")
        @Schema(description = "조회 연월(YYYY-MM)", example = "2026-01")
        String yearMonth
) {
}
