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
                        name = "settlement",
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

    // 정산/발주 기준 날짜
    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    // 해당 일자의 총 발주 건수
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer orderCount;

    // 물건 원가,공급가액 (totalAmount / 1.1)
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal supplyAmount;

    // 부가세 (10%)
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal taxAmount;

    // 가맹점이 본사에 낼 최종 금액 (공급가 + 부가세)
    @Column(name = "total_settlement_amount", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalSettlementAmount;

    @Column(name = "settlement_amount", nullable = false)
    private BigDecimal settlementAmount;

    // 본사 발주 대금 결제(예정)일
    @Column
    private LocalDate payoutDate;

    // 정산 상태 (ORDERED, PAID, COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SettlementStatus status;

    // PDF 영수증의 MinIO 저장 경로를 기록
    @Column(length = 500)
    private String pdfUrl;

    // PDF URL 업데이트를 위한 메서드
    public void updatePdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    @Column(name = "commission_amount", nullable = false, precision = 15, scale = 2) // DB 컬럼명에 맞춤
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal commissionAmount;

    // 해당 일자의 총 주문 금액 (supplyAmount + taxAmount와 같거나 관련됨)
    @Column(name = "total_order_amount", nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalOrderAmount;

    // 상품 ID
    @Column(nullable = true)
    private Long productId;
}