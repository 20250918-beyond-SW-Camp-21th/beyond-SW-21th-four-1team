package com.spicy.backend.settlement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spicy.backend.settlement.application.SettlementFileService;
import com.spicy.backend.settlement.application.SettlementService;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SettlementController.class)
class SettlementControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SettlementService settlementService;

    @MockitoBean
    private SettlementFileService settlementFileService;

    @Nested
    @DisplayName("일별 정산 조회 API 테스트")
    class GetDailySettlement {
        @Test
        @WithMockUser
        @DisplayName("성공: 조건에 맞는 일별 정산 데이터를 JSON으로 반환한다")
        void success() throws Exception {
            // given
            DailySettlementResponse response = DailySettlementResponse.builder()
                    .productId(1L)
                    .orderCount(15)
                    .dailyAmount(new BigDecimal("150000"))
                    .monthlyAccumulatedAmount(new BigDecimal("1000000"))
                    .build();

            given(settlementService.getDailySettlement(any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/settlements/daily")
                            .param("storeId", "1")
                            .param("productId", "1")
                            .param("date", "2026-01-18"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderCount").value(15))
                    .andExpect(jsonPath("$.dailyAmount").value(150000));
        }
    }

    @Nested
    @DisplayName("월별 정산 조회 API 테스트")
    class GetMonthlySettlement {
        @Test
        @WithMockUser
        @DisplayName("성공: 해당 월의 정산 요약 데이터를 반환한다")
        void success() throws Exception {
            // given
            MonthlySettlementResponse response = MonthlySettlementResponse.builder()
                    .totalAmount(new BigDecimal("3000000"))
                    .status(SettlementStatus.ORDERED)
                    .build();

            given(settlementService.getMonthlySettlement(any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/settlements/monthly")
                            .param("storeId", "1")
                            .param("productId", "1")
                            .param("yearMonth", "2026-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalAmount").value(3000000))
                    .andExpect(jsonPath("$.status").value("ORDERED")); // ✅ 수정
        }
    }

    @Nested
    @DisplayName("PDF 다운로드 API 테스트")
    class DownloadMonthlySettlementPdf {
        @Test
        @WithMockUser
        @DisplayName("성공: 월별 정산 데이터를 기반으로 생성된 PDF 파일을 반환한다")
        void success() throws Exception {
            // given
            byte[] mockPdfContent = "%PDF-test-content".getBytes();
            given(settlementService.getMonthlySettlement(any())).willReturn(MonthlySettlementResponse.builder().build());
            given(settlementFileService.createMonthlySettlementPdf(any(MonthlySettlementResponse.class), anyString()))
                    .willReturn(mockPdfContent);

            // when & then
            mockMvc.perform(get("/api/v1/settlements/monthly/download")
                            .param("storeId", "1")
                            .param("productId", "1")
                            .param("yearMonth", "2026-01"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    // ✅ 완전일치 대신 포함 검사 (인코딩/filename* 대응)
                    .andExpect(header().string("Content-Disposition", containsString("attachment")))
                    .andExpect(header().string("Content-Disposition", containsString("receipt_2026-01.pdf")))
                    .andExpect(content().bytes(mockPdfContent));
        }
    }

    @Nested
    @DisplayName("일일 정산 생성 API 테스트")
    class CreateSettlement {
        @Test
        @WithMockUser
        @DisplayName("성공: 요청받은 날짜의 정산 데이터를 생성한다")
        void success() throws Exception {
            // given
            DailySettlementRequest request = new DailySettlementRequest(1L, 1L, LocalDate.of(2026, 1, 18));

            // when & then
            mockMvc.perform(post("/api/v1/settlements/generate")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }
}
