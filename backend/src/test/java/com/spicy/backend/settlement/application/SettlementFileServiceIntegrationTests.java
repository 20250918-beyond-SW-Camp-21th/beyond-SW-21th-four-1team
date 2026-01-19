package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest // 실제 Bean(SpringTemplateEngine 등)을 모두 로드하는 통합 테스트
class SettlementFileServiceIntegrationTests {

    @Autowired
    private SettlementFileService settlementFileService;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("성공: 실제 템플릿과 데이터를 결합하여 유효한 PDF 바이트 데이터를 생성한다")
    void createAndUploadSettlementPdf_IntegrationSuccess() throws Exception {
        // given: 실제 템플릿에 들어갈 데이터 준비
        MonthlySettlementResponse data = MonthlySettlementResponse.builder()
                .totalAmount(new BigDecimal("2500000"))
                .supplyAmount(new BigDecimal("2272727")) // 예시값 (원하면 정확히 계산해도 됨)
                .taxAmount(new BigDecimal("227273"))
                .status(SettlementStatus.ORDERED)
                .payoutDate(LocalDate.of(2026, 2, 10))
                .productId(101L)
                .build();

        // when: 실제 서비스 로직 수행 (Thymeleaf 렌더링 + iText 변환)
        byte[] pdfBytes = settlementFileService.createMonthlySettlementPdf(data, "2026-01");

        // then: 생성된 PDF 검증
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);

        Files.write(tempDir.resolve("test_output_yk.pdf"), pdfBytes); // 임시 디렉터리에 기록

        // PDF 파일의 시작 마커(%PDF-) 확인 (진짜 PDF 형식인지 검증)
        String pdfHeader = new String(pdfBytes, 0, 4, StandardCharsets.US_ASCII);
        assertThat(pdfHeader).isEqualTo("%PDF");
    }
}