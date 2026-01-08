package com.spicy.backend.settlement.domain;

import com.spicy.backend.global.entity.BaseEntity;
import com.spicy.backend.settlement.enums.SettlementStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 가맹점 식별자
    @Column(nullable = false)
    private Long storeId;

    // 정산 기준 날짜
    @Column(nullable = false)
    private LocalDate settlementDate;

    // 주문 건수
    @Column(nullable = false)
    private Integer orderCount;

    // 주문 금액 합계
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalOrderAmount;

    // 수수료 금액 (본사 수익)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal commissionAmount;

    // 최종 정산 금액
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal settlementAmount;

    // 지급 예정일
    private LocalDate payoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

}
