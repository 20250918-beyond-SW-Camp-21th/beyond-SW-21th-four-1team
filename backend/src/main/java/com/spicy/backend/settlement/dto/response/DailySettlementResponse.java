package com.spicy.backend.settlement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "일별 정산 응답")
public record DailySettlementResponse(
       /* @Schema(description = "상품 식별 번호", example = "1")
        Long productId,*/

        @Schema(description = "정산 포함 상품 상세 내역")
        java.util.List<com.spicy.backend.settlement.dto.response.SettlementItemResponse> items,

        @Schema(description = "일일 발주 건수", example = "15")
        Integer orderCount,

        @Schema(description = "일일 총 매입 금액 (지출)", example = "150000.00")
        BigDecimal dailyAmount,

        @Schema(description = "해당 월 누적 매입 금액", example = "4500000.00")
        BigDecimal monthlyAccumulatedAmount
) {}