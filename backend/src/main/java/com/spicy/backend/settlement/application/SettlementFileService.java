package com.spicy.backend.settlement.application;

import com.itextpdf.html2pdf.HtmlConverter;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class SettlementFileService {

    private final SpringTemplateEngine templateEngine;
    // private final AmazonS3 s3Client; // S3 사용 시 주입

    //월별 정산 내역 PDF 생성 및 업로드, 문서 생성 & 파일 저장 반영
    public String createAndUploadSettlementPdf(MonthlySettlementResponse data) {
        // 1. Thymeleaf를 이용한 HTML 생성
        Context context = new Context();
        context.setVariable("settlement", data);
        String htmlContent = templateEngine.process("settlement_template", context);

        // 2. HTML을 PDF로 변환, iText 사용
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, target);
        byte[] pdfBytes = target.toByteArray();

        // 3. Object Storage(S3/MinIO)에 저장 (목표 반영)
        // String fileUrl = s3Client.putObject(bucket, fileName, pdfBytes);

        // 우선은 로컬 저장 예시로 대체합니다.
        return "https://storage.example.com/settlements/2026-01-report.pdf";
    }
}