package com.spicy.backend.inventory.domain;

import com.spicy.backend.global.entity.BaseEntity;
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

@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //상품ID
    @Column(nullable = false)
    private Long productId;

    //수량
    @Column(nullable = false)
    private int quantity;

    //유통기한
    @Column(nullable = false)
    private LocalDate expirationDate;

    //상품상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LotStatus status;

    //상품이름
    @Column(nullable = false)
    private String productName;

    //상품 일련번호
    @Column(nullable = false)
    private String productCode;

    //상품 가격
    @Column(nullable = false)
    private BigDecimal price;

    //상품 설명
    @Column(nullable = false)
    private String description;
}