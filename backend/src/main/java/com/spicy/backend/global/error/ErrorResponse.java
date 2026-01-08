package com.spicy.backend.global.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        String code,
        String message
) {
    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(
                        errorCode.getCode(),
                        errorCode.getMessage()
                ));
    }
}
