package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SettlementFileServiceTests {

    @InjectMocks
    private SettlementFileService settlementFileService;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Test
    @DisplayName("일별 정산 PDF 바이트 생성 시도 - 성공할때")
    void createDailySettlementPdf_Success() {
        // Given
        DailySettlementResponse data = DailySettlementResponse.builder()
                .orderCount(1)
                .dailyAmount(new BigDecimal("1000"))
                .items(List.of())
                .build();
        LocalDate date = LocalDate.of(2026, 1, 21);


        // 템플릿 엔진 동작 Mocking
        given(templateEngine.process(anyString(), any(Context.class)))
                .willReturn("<html><body>Test Receipt</body></html>");

        // Whenimport static org.assertj.core.api.Assertions.assertThat;
        byte[] pdfBytes = settlementFileService.createDailySettlementPdf(data, date);

        // Then
        assertThat(pdfBytes).isNotNull(); // PDF 바이트가 생성되었는지 확인
        assertThat(pdfBytes.length).isGreaterThan(0);

        // "settlement_template"이라는 이름으로 템플릿 엔진이 호출되었는지 확인
        verify(templateEngine).process(eq("settlement_template"), any(Context.class));
    }

    @Test
    @DisplayName("PDF 생성 실패할때 RuntimeException 발생 확인")
    void generatePdfBytes_Failure_ThrowsException() {
        // Given 템플릿 엔진에서 에러가 발생하는 상황 가정
        given(templateEngine.process(anyString(), any(Context.class)))
                .willThrow(new RuntimeException("Template error"));

        // When 예외가 발생하는 행위를 정의 (실행 직전)
        DailySettlementResponse data = DailySettlementResponse.builder()
                .orderCount(1)
                .dailyAmount(new BigDecimal("1000"))
                .items(List.of())
                .build();
        LocalDate date = LocalDate.of(2026, 1, 21);

        org.assertj.core.api.ThrowableAssert.ThrowingCallable execution =
                () -> settlementFileService.createDailySettlementPdf(data, date);
        // Then
        assertThatThrownBy(execution)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("PDF 변환 중 오류가 발생했습니다.");
    }
}