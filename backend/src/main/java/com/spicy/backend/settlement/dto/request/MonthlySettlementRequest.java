package com.spicy.backend.settlement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "월별 정산 요청 DTO")
public record MonthlySettlementRequest(
        @NotNull(message = "가맹점 식별 번호는 필수입니다.")
        @Schema(description = "가맹점 식별 번호", example = "1")
        Long storeId,

        /*@NotNull
        @Schema(description = "상품 식별 번호",example = "1")
        Long productId,*/

        @NotBlank(message = "조회 연월은 필수입니다.")
        @Schema(description = "조회 연월(YYYY-MM)", example = "2026-01", type = "string")
        String yearMonth
) {
}
