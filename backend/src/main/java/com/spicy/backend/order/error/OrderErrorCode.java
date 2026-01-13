package com.spicy.backend.order.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {
    // 주문 존재하지 않음
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않거나 권한이 없습니다", "O001"),

    // 주문 정보 존재하지 않음
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 상품이 존재하지 않거나 권한이 없습니다.", "O002");

    private final HttpStatus status;
    private final String message;
    private final String code;
}
