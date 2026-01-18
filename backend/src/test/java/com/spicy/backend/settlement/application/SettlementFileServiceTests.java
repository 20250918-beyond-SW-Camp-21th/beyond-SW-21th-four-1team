//package com.spicy.backend.settlement.application;
//
//import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
//import com.spicy.backend.settlement.enums.SettlementStatus;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//@SpringBootTest
//class SettlementFileServiceTests {
//
//    @Autowired
//    private SettlementFileService settlementFileService;
//
//    @Test
//    @DisplayName("템플릿 파일을 읽어서 PDF 생성 로직이 에러 없이 돌아가는지 확인")
//     void createdPdfTest() {
//        //given : 더미 데이터 생성
//        MonthlySettlementResponse data = MonthlySettlementResponse.builder()
//                .totalAmount(new BigDecimal("100000"))
//                .commissionAmount(new BigDecimal("10000"))
//                .settlementAmount(new BigDecimal("90000"))
//                .status(SettlementStatus.WAITING)
//                .payoutDate(LocalDate.now())
//                .build();
//
//        // when : PDF 생성 메서드 실행
//        byte[] resultFile = settlementFileService.createAndUploadSettlementPdf(data);
//
//        // then : 파일 데이터가 비어있지 않은지 확인합니다
//        System.out.println("생성된 파일 크기: " + resultFile.length + " bytes");
//
//        assertThat(resultFile).isNotNull();   // null이 아니어야 함
//        assertThat(resultFile).isNotEmpty();  // 크기가 0보다 커야 함
//    }
//}