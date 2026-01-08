package com.spicy.backend.global.error.exception;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends BaseException{

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
