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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(
    uniqueConstraints = {
    @UniqueConstraint(
            name = "settlement_store_date",
            columnNames = {"store_id", "settlement_date"}
        )
    }
)
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 가맹점 식별자
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    // 정산 기준 날짜
    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    // 주문 건수
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer orderCount;

    // 주문 금액 합계
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalOrderAmount;

    // 수수료 금액 (본사 수익)
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal commissionAmount;

    // 최종 정산 금액
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal settlementAmount;

    // 지급 예정일
    private LocalDate payoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SettlementStatus status;

}
