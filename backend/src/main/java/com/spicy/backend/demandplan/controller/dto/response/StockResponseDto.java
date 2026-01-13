package com.spicy.backend.demandplan.controller.dto.response;

import com.spicy.backend.inventory.dto.response.InventoryLotResponse;

import java.math.BigDecimal;
import java.util.List;

public record StockResponseDto(
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
) {}
