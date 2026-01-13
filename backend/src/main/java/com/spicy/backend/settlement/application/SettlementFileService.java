package com.spicy.backend.settlement.application;

import com.itextpdf.html2pdf.HtmlConverter;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementFileService {

    private final SpringTemplateEngine templateEngine;

    //월별 정산 내역 PDF 생성 및 업로드, 문서 생성 & 파일 저장 반영
    public byte[] createAndUploadSettlementPdf(MonthlySettlementResponse data) {
        try {
            // 1. Thymeleaf를 이용한 HTML 생성
            Context context = new Context();
            context.setVariable("settlement", data);
            String htmlContent = templateEngine.process("settlement_template", context);

            // 2. HTML을 PDF로 변환, iText 사용
            try (ByteArrayOutputStream target = new ByteArrayOutputStream()) {
                HtmlConverter.convertToPdf(htmlContent, target);

                // 로그 메시지 변경 (URL 반환 -> PDF 데이터 반환)
                log.info("PDF 생성 완료: totalAmount={}, size={}, bytes",
                        data.totalAmount(), target.size());

                //실제 생성된 PDF 파일 데이터(byte[])를 반환
                return target.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("정산 PDF 생성 실패", e);
        }
    }
}