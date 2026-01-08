package com.spicy.backend.global.error.exception;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public BaseException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
