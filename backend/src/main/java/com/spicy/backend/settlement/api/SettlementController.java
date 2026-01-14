package com.spicy.backend.settlement.api;


import com.spicy.backend.settlement.application.SettlementFileService;
import com.spicy.backend.settlement.application.SettlementService;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Settlement", description = "정산 API (일별/월별 조회)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final SettlementService settlementService;
    private final SettlementFileService settlementFileService;

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

    // PDF 다운로드 API
    @Operation(summary = "월별 정산 명세서 다운로드 (PDF)")
    @GetMapping("/monthly/download")
    public ResponseEntity<byte[]> downloadMonthlySettlementPdf(
            @Valid MonthlySettlementRequest request) {

        // 데이터 조회
        MonthlySettlementResponse data = settlementService.getMonthlySettlement(request);

        // PDF 생성
        byte[] pdfFile = settlementFileService.createAndUploadSettlementPdf(data);

        // 3. 파일 다운로드 응답 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=settlement_" + request.yearMonth() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfFile);
    }

    @Operation(summary = "일일 정산 생성") // 이게 있을 수도 있고 없을 수도 있음
    @PostMapping("/generate")            // 핵심은 이 줄입니다!
    public ResponseEntity<Void> createSettlement(
            @RequestParam Long storeId,
            @RequestParam LocalDate date
    ) {
        settlementService.createSettlement(storeId, date);
        return ResponseEntity.ok().build();
    }


}

