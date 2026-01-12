package com.spicy.backend.inventory.dto.response;

import java.math.BigDecimal;

public record ProductBaseInfo(
        Long productId,
        String productName,
        BigDecimal price,
        int minimumQuantity
) {}