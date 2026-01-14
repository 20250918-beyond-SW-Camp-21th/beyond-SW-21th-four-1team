package com.spicy.backend.settlement.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode { // 기존 프로젝트 구조에 맞게 implements

    // 정산 데이터 부재 에러
    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 날짜의 정산 내역이 존재하지 않습니다.", "S001"),

    // 월간 누적 데이터 부재
    MONTHLY_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 월의 정산 내역을 찾을 수 없습니다.", "S002"),

    SETTLEMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 날짜의 정산 내역이 존재합니다.", "S003");

    private final HttpStatus status;
    private final String message;
    private final String code;
}
