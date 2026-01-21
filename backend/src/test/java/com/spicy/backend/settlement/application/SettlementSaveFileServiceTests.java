package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

    @ExtendWith(MockitoExtension.class)
    class SettlementSaveFileTests {

        @InjectMocks
        private SettlementFileService settlementFileService;

        @Mock
        private SpringTemplateEngine templateEngine;

        // JUnit 5 - 임시 디렉토리 생성 기능 (테스트 종료 후 자동 삭제)
        @TempDir
        Path tempDir;

        @BeforeEach
        void setUp() {
            // @Value 필드에 테스트용 임시 경로를 강제로 주입
            ReflectionTestUtils.setField(settlementFileService, "uploadDir", tempDir.toString());
        }

        @Test
        @DisplayName("일별 정산 PDF 생성 및 로컬 저장 성공")
        void saveDailySettlementPdf_Success() {
            // Given
            DailySettlementResponse data = DailySettlementResponse.builder()
                    .orderCount(3)
                    .dailyAmount(new BigDecimal("30000"))
                    .items(List.of())
                    .build();
            LocalDate date = LocalDate.now();

            given(templateEngine.process(anyString(), any(Context.class)))
                    .willReturn("<html><body>Receipt Content</body></html>");

            // When 내부적으로 createDailySettlementPdf를 호출하고 파일 저장함
            String savedPath = settlementFileService.saveDailySettlementPdf(data, date);

            // Then 경로가 반환되었는지 확인
            assertThat(savedPath).isNotNull();

            File savedFile = new File(savedPath);
            assertThat(savedFile.exists()).isTrue(); // 실제로 파일이 생성되었는지 확인
            assertThat(savedFile.getName()).startsWith("daily_receipt_" + date); // 파일명 규칙 확인
        }
    }