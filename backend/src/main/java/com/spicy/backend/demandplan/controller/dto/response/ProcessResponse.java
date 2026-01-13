package com.spicy.backend.demandplan.controller.dto.response;

public record ProcessResponse(
        boolean isOrderRequired,    // 주문 필요 여부
        String message              // 출력 메세지
) {
}
