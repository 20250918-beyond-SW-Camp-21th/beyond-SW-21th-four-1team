package com.spicy.backend.inventory.domain;

public enum LotStatus {
    ACTIVE,     // 사용 중
    CONSUMED,   // 재고 소진
    EXPIRED     // 유통기한 만료
}
