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

@SpringBootTest
class SettlementFileServiceIntegrationTests {

    @Autowired
    private SettlementFileService settlementFileService;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("성공: 실제 템플릿과 데이터를 결합하여 유효한 PDF 바이트 데이터를 생성한다")
    void createMonthlyPdf_IntegrationSuccess() throws Exception {
        MonthlySettlementResponse data = MonthlySettlementResponse.builder()
                .totalAmount(new BigDecimal("2500000"))
                .supplyAmount(new BigDecimal("2272727"))
                .taxAmount(new BigDecimal("227273"))
                .status(SettlementStatus.ORDERED)
                .payoutDate(LocalDate.of(2026, 2, 10))
                .build();

        byte[] pdfBytes = settlementFileService.createMonthlySettlementPdf(data, "2026-01");

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);

        Files.write(tempDir.resolve("test_output.pdf"), pdfBytes);

        String pdfHeader = new String(pdfBytes, 0, 4, StandardCharsets.US_ASCII);
        assertThat(pdfHeader).isEqualTo("%PDF");
    }
}
