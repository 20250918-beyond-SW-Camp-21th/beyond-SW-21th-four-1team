package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest // 실제 Bean(SpringTemplateEngine 등)을 모두 로드하는 통합 테스트
class SettlementFileServiceIntegrationTests {

    @Autowired
    private SettlementFileService settlementFileService;

    @Test
    @DisplayName("성공: 실제 템플릿과 데이터를 결합하여 유효한 PDF 바이트 데이터를 생성한다")
    void createAndUploadSettlementPdf_IntegrationSuccess() throws Exception {
        // given: 실제 템플릿에 들어갈 데이터 준비
        MonthlySettlementResponse data = MonthlySettlementResponse.builder()
                .totalAmount(new BigDecimal("2500000"))
                .commissionAmount(new BigDecimal("125000"))
                .settlementAmount(new BigDecimal("2375000"))
                .status(SettlementStatus.WAITING)
                .payoutDate(LocalDate.of(2026, 2, 10))
                .productId(101L)
                .build();

        // when: 실제 서비스 로직 수행 (Thymeleaf 렌더링 + iText 변환)
        byte[] pdfBytes = settlementFileService.createAndUploadSettlementPdf(data);

        // then: 생성된 PDF 검증
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);

        Files.write(Paths.get("test_output_yk.pdf"), pdfBytes); //실제 pdf를 보기위해 작성

        // PDF 파일의 시작 마커(%PDF-) 확인 (진짜 PDF 형식인지 검증)
        String pdfHeader = new String(pdfBytes, 0, 4);
        assertThat(pdfHeader).isEqualTo("%PDF");

        System.out.println("통합 테스트 성공 - 생성된 PDF 크기: " + pdfBytes.length + " bytes");
    }
}