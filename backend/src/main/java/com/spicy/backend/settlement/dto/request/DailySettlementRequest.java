package com.spicy.backend.settlement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record DailySettlementRequest(
            @NotNull(message = "가맹점 식별 번호는 필수입니다.")
            @Schema(description = "가맹점 식별 번호", example = "1")
            Long storeId,

            /*@NotNull
            @Schema(description = "상품 식별 번호",example = "1")
            Long productId,*/

            @NotNull(message = "조회 날짜는 필수입니다.")
            @PastOrPresent(message = "미래 날짜의 정산은 생성하거나 조회할 수 없습니다.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Schema(description = "조회 날짜 (YYYY-MM-DD)", example = "2026-01-09")
            LocalDate date
    ) {}
