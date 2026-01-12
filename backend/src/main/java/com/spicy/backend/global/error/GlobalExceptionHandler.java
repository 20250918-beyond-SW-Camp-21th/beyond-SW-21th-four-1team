package com.spicy.backend.global.error;

import com.spicy.backend.global.common.ApiResponse;
import com.spicy.backend.global.error.errorcode.GlobalErrorCode;
import com.spicy.backend.global.error.exception.BusinessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(BusinessException e) {

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {

        String combinedMessage = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(combinedMessage));
    }
}
