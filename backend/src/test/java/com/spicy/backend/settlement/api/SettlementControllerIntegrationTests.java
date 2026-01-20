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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc.perform(get("/api/v1/settlements/monthly/download")
                        .param("storeId", "1")
                        .param("yearMonth", "2026-01")
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    assertThat(content).isNotEmpty();
                    // 진짜 PDF 헤더 확인(더 강하게)
                    assertThat(new String(content, 0, 4)).isEqualTo("%PDF");
                });
    }
}
