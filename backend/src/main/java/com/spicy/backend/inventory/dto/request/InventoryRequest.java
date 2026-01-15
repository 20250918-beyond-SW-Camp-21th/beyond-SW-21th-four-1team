package com.spicy.backend.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InventoryRequest(
                // 상품ID
                @NotNull(message = "상품 이름은 필수입니다.") String name,

                // 상품 수량
                @Min(value = 1, message = "수량은 1 이상이어야 합니다.") int quantity,

                // 유통기한
                @NotNull(message = "유통기한은 필수입니다.") LocalDate expirationDate,

                // 상품 일련번호
                @NotNull(message = "일련번호는 필수입니다.") String productCode) {
}
