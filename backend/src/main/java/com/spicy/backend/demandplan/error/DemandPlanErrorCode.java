package com.spicy.backend.demandplan.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DemandPlanErrorCode implements BaseErrorCode {

    // 판매 내역 조회 실패
    FAILED_TO_FETCH_SALES(HttpStatus.NOT_FOUND, "해당 상품의 주문 내역을 찾을 수 없습니다.", "D001"),
    // 상품 조회 실패
    FAILED_TO_FETCH_PRODUCTS(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다.", "D002");


    private final HttpStatus status;
    private final String message;
    private final String code;
}
