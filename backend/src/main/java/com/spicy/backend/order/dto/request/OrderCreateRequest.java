package com.spicy.backend.order.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record OrderCreateRequest(
        // 희망 배송지
        @NotNull
        LocalDate deliveryDate,

        // 주소
        @NotNull
        String address,

        // 수령인 이름
        @NotNull
        String receiverName,

        // 수령인 연락처
        @NotNull
        String receiverPhone,

        // 배송 요청사항
        String memo
) {
}
