package com.spicy.backend.settlement.domain;

import com.spicy.backend.global.entity.BaseEntity;
import com.spicy.backend.settlement.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer orderCount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalOrderAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal commissionAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal settlementAmount;

    private LocalDate payoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

}
