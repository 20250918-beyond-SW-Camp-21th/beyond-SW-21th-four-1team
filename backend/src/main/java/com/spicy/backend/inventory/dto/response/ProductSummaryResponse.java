package com.spicy.backend.inventory.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductSummaryResponse(
        // 상품 ID
        Long productId,
        // 상품 이름
        String productName,
        // 상품 가격
        BigDecimal price,
        // 최소 수량
        int minimumQuantity,
        // 총 수량
        int totalQuantity,
        //총 상품
        List<InventoryLotResponse> products
) {
}
