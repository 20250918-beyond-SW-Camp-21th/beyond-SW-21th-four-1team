package com.spicy.backend.order.dto.response;

import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        @NotNull
        Long orderId,

        @NotBlank
        String orderNumber,

        @NotNull
        @Min(1)
        BigDecimal totalAmount,

        @NotNull
        Status status,

        @NotNull
        LocalDate deliveryDate,

        @NotBlank
        String address,

        @NotBlank
        String receiverName,

        @NotBlank
        String receiverPhone,

        String memo,

        @NotNull
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getDeliveryDate(),
                order.getAddress(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getMemo(),
                order.getCreatedAt()
        );
    }

    public static List<OrderResponse> from(List<Order> orders) {
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }
}
