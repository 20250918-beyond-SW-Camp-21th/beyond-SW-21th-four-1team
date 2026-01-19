package com.spicy.backend.settlement.error;

import com.spicy.backend.global.error.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode { // 기존 프로젝트 구조에 맞게 implements

    // 조회 관련 에러
    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 날짜의 정산 내역이 존재하지 않습니다.", "S001"),
    MONTHLY_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 월의 정산 내역을 찾을 수 없습니다.", "S002"),

    // 생성 및 중복 관련 에러
    SETTLEMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 날짜의 정산 내역이 존재합니다.", "S003"),
    NO_ORDERS_FOR_SETTLEMENT(HttpStatus.NOT_FOUND, "해당 날짜에 배송 완료된 주문이 없어 정산을 생성할 수 없습니다.", "S004"),

    // 정확한 연산 및 PDF 생성 관련 에러
    INVALID_SETTLEMENT_AMOUNT(HttpStatus.BAD_REQUEST, "정산 금액 계산이 올바르지 않습니다.", "S005"),
    PDF_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PDF 영수증 생성 중 오류가 발생했습니다.", "S006"),

    // 로컬 파일 시스템 관련 에러
    FILE_STORAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 시스템에 영수증을 저장하지 못했습니다.", "S007"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "서버에서 해당 영수증 파일을 찾을 수 없습니다.", "S008");

    private final HttpStatus status;
    private final String message;
    private final String code;
}
