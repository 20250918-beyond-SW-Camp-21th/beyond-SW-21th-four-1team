package com.spicy.backend.order.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {
    // 에러 예시
    ERROR_EX(HttpStatus.NOT_FOUND, "예시 에러입니다", "P001");

    private final HttpStatus status;
    private final String message;
    private final String code;
}
