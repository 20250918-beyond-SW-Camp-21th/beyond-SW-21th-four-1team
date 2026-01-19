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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SettlementFileServiceTests {

    @Autowired
    private SettlementFileService settlementFileService;

    @Nested
    @DisplayName("월별 정산 PDF 생성 테스트")
    class CreateAndUploadSettlementPdf {

        @Test
        @DisplayName("성공: 유효한 데이터를 전달하면 PDF 바이트 배열이 생성되어야 한다")
        void success() {
            // given: 월별 정산 더미 데이터 생성
            MonthlySettlementResponse data = MonthlySettlementResponse.builder()
                    .productId(1L)
                    .totalAmount(new BigDecimal("5000000"))
                    .commissionAmount(new BigDecimal("250000"))
                    .settlementAmount(new BigDecimal("4750000"))
                    .status(SettlementStatus.WAITING)
                    .payoutDate(LocalDate.of(2026, 2, 10))
                    .build();

            // when: PDF 생성 메서드 호출
            byte[] pdfResult = settlementFileService.createAndUploadSettlementPdf(data);

            // then: 결과 검증
            assertThat(pdfResult).isNotNull();
            assertThat(pdfResult.length).isGreaterThan(0);

            // PDF 파일의 헤더(%PDF-) 확인으로 실제 PDF 형식인지 검증
            String header = new String(pdfResult, 0, 4);
            assertThat(header).isEqualTo("%PDF");
        }

        @Test
        @DisplayName("실패: 데이터가 null인 경우 PDF 생성 과정에서 예외가 발생해야 한다")
        void fail_WhenDataIsNull() {
            // when & then
            assertThatThrownBy(() -> settlementFileService.createAndUploadSettlementPdf(null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("정산 PDF 생성 실패");
        }
    }
}