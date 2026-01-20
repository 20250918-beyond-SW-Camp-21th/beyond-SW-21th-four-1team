package com.spicy.backend.settlement.application;

import com.itextpdf.html2pdf.HtmlConverter;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementFileService {

    private final SpringTemplateEngine templateEngine;

    @Value("${file.upload-dir:./uploads/settlements}")
    private String uploadDir;

    /**
     * [컨트롤러용] 일별 PDF 바이트 생성 (즉시 다운로드용)
     */

    public byte[] createDailySettlementPdf(DailySettlementResponse data, LocalDate date) {

        if (data == null || data.items() == null || data.items().isEmpty()) {
            log.warn("정산 데이터가 비어있습니다. 날짜: {}", date);
            // 필요시 예외 처리
        }


        Context context = new Context();
        context.setVariable("type", "DAILY");
        context.setVariable("receipt", data);
        context.setVariable("targetDate", date);
        return generatePdfBytes("settlement_template", context);
    }

    /**
     * [컨트롤러용] 월별 PDF 바이트 생성 (즉시 다운로드용)
     */
    public byte[] createMonthlySettlementPdf(MonthlySettlementResponse data, String yearMonth) {
        Context context = new Context();
        context.setVariable("type", "MONTHLY");
        context.setVariable("receipt", data);
        context.setVariable("targetDate", yearMonth);
        return generatePdfBytes("settlement_template", context);
    }

    /**
     * [서비스용] 일별 PDF 생성 및 로컬 저장 (경로 반환)
     */
    public String saveDailySettlementPdf(DailySettlementResponse data, LocalDate date) {
        String fileName = "daily_receipt_" + date + "_" + System.currentTimeMillis() + ".pdf";
        byte[] content = createDailySettlementPdf(data, date);
        return storeFile(content, fileName);
    }

    private byte[] generatePdfBytes(String templateName, Context context) {
        try (ByteArrayOutputStream target = new ByteArrayOutputStream()) {
            String htmlContent = templateEngine.process(templateName, context);
            HtmlConverter.convertToPdf(htmlContent, target);
            return target.toByteArray();
        } catch (Exception e) {
            log.error("PDF 바이트 생성 실패: {}", e.getMessage());
            throw new RuntimeException("PDF 변환 중 오류가 발생했습니다.");
        }
    }

    private String storeFile(byte[] content, String fileName) {
        try {
            Path directoryPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directoryPath);
            Path filePath = directoryPath.resolve(fileName);
            Files.write(filePath, content);
            log.info("파일 저장 성공: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("파일 시스템 저장 실패: {}", e.getMessage());
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }
    }
}