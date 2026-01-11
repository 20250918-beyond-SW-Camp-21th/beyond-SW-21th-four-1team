package com.spicy.backend.settlement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MonthlySettlementRequest(
        @NotNull
        @Schema(description = "가맹점 식별 번호", example = "1")
        Long storeId,

        @NotNull
        @Schema(description = "조회 연월(YYYY-MM)", example = "2026-01")
        String yearMonth
) {
}
