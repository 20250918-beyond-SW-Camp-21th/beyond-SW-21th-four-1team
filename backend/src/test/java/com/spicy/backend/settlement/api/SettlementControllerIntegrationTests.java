package com.spicy.backend.settlement.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SettlementControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("월간 정산 PDF 다운로드 통합 테스트 - 실제 PDF 생성 확인")
    @WithMockUser
    void downloadMonthlySettlementIntegrationTest() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/v1/settlements/monthly/download")
                        .param("storeId", "1")
                        .param("productId", "10")
                        .param("yearMonth", "2026-01")
                        .accept(MediaType.APPLICATION_PDF)) // PDF 요청
                .andExpect(status().isOk()) // 200 OK 확인
                .andExpect(content().contentType(MediaType.APPLICATION_PDF)) // 진짜 PDF 타입인지 확인
                .andExpect(header().exists("Content-Disposition")) // 다운로드 헤더 확인
                .andExpect(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    // 진짜 PDF 파일이 생성되어 바이트가 채워졌는지 확인 (0보다 커야 함)
                    assertThat(content).isNotEmpty();
                });
    }
}