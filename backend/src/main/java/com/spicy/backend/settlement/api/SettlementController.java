package com.spicy.backend.settlement.api;

import com.spicy.backend.order.dto.response.OrderResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Settlement", description = "정산 API (본사 매입 내역 및 영수증)")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final SettlementService settlementService;
    private final SettlementFileService settlementFileService;

    @Operation(
            summary = "일별 매입 내역 조회",
            description = "특정 날짜의 본사 발주 건수, 일 매입 금액, 해당 월 누적 매입 금액과 상세 품목 리스트를 조회합니다."
    )
    @GetMapping("/daily")
    public ResponseEntity<DailySettlementResponse> getDailySettlement(
            @Valid DailySettlementRequest request) {
        DailySettlementResponse response = settlementService.getDailySettlement(request);
        log.info("DailySettlement=======> {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "일별 매입 영수증 다운로드 (PDF)",
            description = "특정 날짜의 물품 매입 상세 내역이 담긴 영수증 PDF를 다운로드합니다."
    )
    @GetMapping("/daily/download")
    public ResponseEntity<byte[]> downloadDailySettlementPdf(
            @Valid DailySettlementRequest request) {

        // 1. 일별 데이터 조회 (상세 품목 포함)
        DailySettlementResponse responseData = settlementService.getDailySettlement(request);

        // 2. 일별 PDF 생성
        byte[] pdfFile = settlementFileService.createDailySettlementPdf(responseData, request.date());

        // 3. 응답 처리
        return createPdfResponse(pdfFile, "daily_receipt_" + request.date() + ".pdf");
    }

    @Operation(
            summary = "월별 매입 정산 조회",
            description = "특정 월의 총 매입 금액, 공급가액, 부가세, 결제 상태 및 월간 전체 품목 상세를 조회합니다."
    )
    @GetMapping("/monthly")
    public ResponseEntity<MonthlySettlementResponse> getMonthlySettlement(
            @Valid MonthlySettlementRequest request) {
        MonthlySettlementResponse response = settlementService.getMonthlySettlement(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "월별 매입 영수증 다운로드 (PDF)",
            description = "선택한 월의 물품 매입 상세 내역이 담긴 영수증 PDF를 다운로드합니다."
    )
    @GetMapping("/monthly/download")
    public ResponseEntity<byte[]> downloadMonthlySettlementPdf(
            @Valid MonthlySettlementRequest request) {

        // 1. 월별 데이터 조회 (상세 품목 포함)
        MonthlySettlementResponse responseData = settlementService.getMonthlySettlement(request);

        // 2. 월별 PDF 생성
        byte[] pdfFile = settlementFileService.createMonthlySettlementPdf(responseData, request.yearMonth());

        // 3. 응답 처리
        return createPdfResponse(pdfFile, "receipt_" + request.yearMonth() + ".pdf");
    }

    @Operation(
            summary = "정산 포함 주문 상세 조회 (팝업용)",
            description = "특정 정산 일자에 포함된 실제 본사 발주(Order) 리스트를 조회합니다."
    )
    @GetMapping("/details")
    public ResponseEntity<List<OrderResponse>> getSettlementOrderDetails(
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<OrderResponse> response = settlementService.getOrdersBySettlementDate(storeId, date);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "기간별 정산 내역 조회 (그래프용)",
            description = "주간/월간 매입 추이를 그리기 위해 특정 기간의 정산 데이터를 리스트로 조회합니다."
    )
    @GetMapping("/stats")
    public ResponseEntity<List<DailySettlementResponse>> getSettlementStats(
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailySettlementResponse> stats = settlementService.getSettlementStats(storeId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "본사 발주 기반 매입 정산 생성",
            description = "특정 날짜에 완료된 발주 데이터를 모두 수집하여 통합 정산을 생성하고 PDF를 로컬에 저장합니다."
    )
    @PostMapping("/generate")
    public ResponseEntity<String> createSettlement(
            @Valid @RequestBody DailySettlementRequest request
    ) {
        // [수정] 특정 productId가 아닌 가맹점 일괄 정산을 위해 storeId와 date만 전달
        settlementService.createSettlement(request.storeId(), request.date());
        return ResponseEntity.ok("정산 데이터 및 PDF 영수증 생성 완료");
    }

    @Operation(summary = "정산 내역 목록 조회", description = "가맹점의 전체 정산 내역 요약을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<DailySettlementResponse>> getSettlementList(@RequestParam Long storeId) {
        return ResponseEntity.ok(settlementService.getSettlementList(storeId));
    }

    @Operation(summary = "영수증 다운로드", description = "정산 ID를 통해 저장된 PDF 영수증을 다운로드합니다.")
    @GetMapping("/{settlementId}/download")
    public ResponseEntity<Resource> downloadStoredPdf(@PathVariable Long settlementId) {
        Resource resource = settlementService.getPdfFileAsResource(settlementId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * PDF 응답을 위한 공통 ResponseEntity 생성 메서드
     */
    private ResponseEntity<byte[]> createPdfResponse(byte[] pdfFile, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfFile.length)
                .body(pdfFile);
    }
}