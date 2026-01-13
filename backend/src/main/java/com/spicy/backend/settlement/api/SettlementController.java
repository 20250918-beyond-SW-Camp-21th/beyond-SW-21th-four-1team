package com.spicy.backend.settlement.api;


import com.spicy.backend.settlement.application.SettlementService;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Settlement", description = "정산 API (일별/월별 조회)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    //일별 정산 조회 API

    @Operation(
            summary = "일별 정산 조회",
            description = "특정 날짜의 주문 건수, 일 주문 금액, 월 누적 금액 조회"
    )

    @GetMapping("/daily")
    public ResponseEntity<DailySettlementResponse> getDailySettlement(
            @Valid DailySettlementRequest request) {
        DailySettlementResponse response = settlementService.getDailySettlement(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "월별 정산 조회",
            description = "특정 월의 주문 금액 합계, 수수료, 최종 정산 금액, 상태, 지급 예정일 조회"
    )

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySettlementResponse> getMonthlySettlement(
            @Valid MonthlySettlementRequest request) {
        MonthlySettlementResponse response = settlementService.getMonthlySettlement(request);
        return ResponseEntity.ok(response);
    }
}
