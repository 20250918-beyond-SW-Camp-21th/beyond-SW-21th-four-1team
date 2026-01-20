package com.spicy.backend.settlement.dto.response;

import com.spicy.backend.settlement.enums.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(description = "월별 정산 응답")
public record MonthlySettlementResponse(

        @Schema(description = "월간 정산 포함 품목 상세 내역")
        List<SettlementItemResponse> items,

        /*@Schema(description = "상품 식별 번호", example = "1")
        Long productId,*/

        @Schema(description = "해당 월 총 매입 금액 (합계)", example = "5000000.00")
        BigDecimal totalAmount,

        @Schema(description = "공급가액", example = "4545455.00")
        BigDecimal supplyAmount,

        @Schema(description = "부가세 (10%)", example = "454545.00")
        BigDecimal taxAmount,

        @Schema(description = "정산/결제 상태 (ORDERED, PAID, COMPLETED)", example = "ORDERED")
        SettlementStatus status,

        @Schema(description = "지급(결제) 완료일", example = "2026-02-10")
        LocalDate payoutDate
) {
}