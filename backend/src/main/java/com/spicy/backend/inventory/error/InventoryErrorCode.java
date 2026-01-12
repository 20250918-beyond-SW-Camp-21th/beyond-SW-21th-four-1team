package com.spicy.backend.inventory.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InventoryErrorCode implements BaseErrorCode {
    // 재고 부족 Error
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다.", "G009"),
    // 상품 Not Found Error
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다.", "G010");

    private final HttpStatus status;
    private final String message;
    private final String code;

}
