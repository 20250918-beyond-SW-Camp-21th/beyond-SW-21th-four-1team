package com.spicy.backend.settlement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {
    // 본사로 발주를 넣은 직후 (결제 대기 상태)
    PENDING("ORDERED", "발주 완료"),

    // 가맹점주가 대금 지불을 완료했을 때
    PAID("PAID", "결제 완료"),

    // 로컬 서버에 PDF 영수증 저장이 최종 완료되었을 때
    COMPLETED("COMPLETED", "영수증 발행 완료"),

    // 정산 데이터 생성 중 오류가 났거나 취소되었을 때
    FAILED("FAILED", "정산 실패");

    private final String key;
    private final String value;
}
