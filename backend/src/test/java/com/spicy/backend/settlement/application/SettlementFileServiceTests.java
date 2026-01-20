package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SettlementFileServiceTests {

    @Autowired
    private SettlementFileService settlementFileService;

    @Nested
    @DisplayName("월별 정산 PDF 생성 테스트")
    class CreateMonthlySettlementPdf {

        @Test
        @DisplayName("성공: 유효한 데이터를 전달하면 PDF 바이트 배열이 생성되어야 한다")
        void success() {
            MonthlySettlementResponse data = MonthlySettlementResponse.builder()
                    .totalAmount(new BigDecimal("5000000"))
                    .supplyAmount(new BigDecimal("4545455"))
                    .taxAmount(new BigDecimal("454545"))
                    .status(SettlementStatus.ORDERED)
                    .payoutDate(LocalDate.of(2026, 2, 10))
                    .build();

            byte[] pdfResult = settlementFileService.createMonthlySettlementPdf(data, "2026-01");

            assertThat(pdfResult).isNotNull();
            assertThat(pdfResult.length).isGreaterThan(0);

            String header = new String(pdfResult, 0, 4);
            assertThat(header).isEqualTo("%PDF");
        }

        @Test
        @DisplayName("성공: 데이터가 null이어도 템플릿이 허용하면 PDF가 생성될 수 있다")
        void success_WhenDataIsNull() {
            byte[] pdfResult = settlementFileService.createMonthlySettlementPdf(null, "2026-01");

            assertThat(pdfResult).isNotNull();
            assertThat(pdfResult.length).isGreaterThan(0);

            String header = new String(pdfResult, 0, 4);
            assertThat(header).isEqualTo("%PDF");
        }
    }
}
