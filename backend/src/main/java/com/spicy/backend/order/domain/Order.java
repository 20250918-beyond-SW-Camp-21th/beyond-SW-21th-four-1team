package com.spicy.backend.order.domain;

import com.spicy.backend.global.entity.BaseEntity;
import com.spicy.backend.order.dto.request.OrderCreateRequest;
import com.spicy.backend.order.enums.Status;
import com.spicy.backend.order.util.OrderNumberGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문번호
    @Column(nullable = false, unique = true)
    private String orderNumber;

    // fk: 가맹점 식별 번호
    @Column(nullable = false)
    private Long storeId;

    // fk: 주문 생성자 userId
    @Column(nullable = false)
    private Long userId;

    // 총 주문 금액
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // 주문 상태
    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    // 희망 배송일
    @Column(nullable = false)
    private LocalDate deliveryDate;

    // 주소
    @Column(nullable = false)
    private String address;

    // 수령인 이름
    @Column(nullable = false)
    private String receiverName;

    // 수령인 연락처
    @Column(nullable = false)
    private String receiverPhone;

    // 배송 요청사항
    @Column(nullable = false)
    private String memo;

    // 주문 일시 - createdAt

    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalAmount = totalPrice;
    }

    public static Order create(Long userId, OrderCreateRequest request, Long storeId) {
        return Order.builder()
                .userId(userId)
                .storeId(storeId)
                .deliveryDate(request.deliveryDate())
                .address(request.address())
                .receiverName(request.receiverName())
                .receiverPhone(request.receiverPhone())
                .memo(request.memo())
                .status(Status.PENDING)
                .orderNumber(OrderNumberGenerator.generate())
                .totalAmount(BigDecimal.ZERO)
                .build();
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
