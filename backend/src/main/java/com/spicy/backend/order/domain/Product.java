package com.spicy.backend.order.domain;

import com.spicy.backend.global.entity.BaseEntity;
import com.spicy.backend.order.enums.Category;
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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // 상품 코드
    @Column(nullable = false)
    private String productCode;

    // 상품 이름
    @Column(nullable = false)
    private String productName;

    // 상품 카테고리
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    // 판매 단가
    @Column(nullable = false)
    private BigDecimal price;

    // 매입 원가
    @Column(nullable = false)
    private BigDecimal costPrice;

    // 상품 설명
    @Column(nullable = false)
    private String description;

    // 상품 이미지 URL
    @Column(nullable = false)
    private String url;

    // 판매 상태
    @Column(nullable = false)
    private Boolean isActive;

    // 유통기한
    @Column(nullable = false)
    private LocalDate expirationDate;

    // 안전 재고 수량 -> 가맹점 기준
    @Column(nullable = false)
    private Integer safetyStock;

    // 발주 시 배송 소요 시간
    @Column(nullable = false)
    private Integer leadTime;

    // 단위 - EA(개), BOX(박스), KG(킬로그램)
    @Column(nullable = false)
    private String unit;

    // 주 거래처/공급사
}
