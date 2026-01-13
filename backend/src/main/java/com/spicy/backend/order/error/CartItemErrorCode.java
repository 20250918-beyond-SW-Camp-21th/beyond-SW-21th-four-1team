package com.spicy.backend.order.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartItemErrorCode implements BaseErrorCode {
    // 장바구니 상품이 존재하지 않음
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 해당 상품이 존재하지 않거나 권한이 없습니다.", "C001");

    private final HttpStatus status;
    private final String message;
    private final String code;
}
