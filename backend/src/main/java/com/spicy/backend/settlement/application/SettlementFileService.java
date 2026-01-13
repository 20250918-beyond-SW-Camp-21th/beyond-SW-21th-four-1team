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
    // private final AmazonS3 s3Client; // S3 사용 시 주입

    //월별 정산 내역 PDF 생성 및 업로드, 문서 생성 & 파일 저장 반영
    public String createAndUploadSettlementPdf(MonthlySettlementResponse data) {
        try {
            // 1. Thymeleaf를 이용한 HTML 생성
            Context context = new Context();
            context.setVariable("settlement", data);
            String htmlContent = templateEngine.process("settlement_template", context);

            // 2. HTML을 PDF로 변환, iText 사용
            try (ByteArrayOutputStream target = new ByteArrayOutputStream()) {
                HtmlConverter.convertToPdf(htmlContent, target);
                byte[] pdfBytes = target.toByteArray();

                // 3. [로그 추가 위치] 실제 반환 직전
                log.warn("Object Storage 업로드 미구현 - 임시 URL 반환: totalAmount={}", data.totalAmount()); //

                // TODO: 실제 S3 또는 MinIO 업로드 로직으로 교체 필요
                return "https://storage.example.com/settlements/2026-01-report.pdf";
            }
        } catch (Exception e) {
            throw new RuntimeException("정산 PDF 생성 실패", e);
        }
    }
}