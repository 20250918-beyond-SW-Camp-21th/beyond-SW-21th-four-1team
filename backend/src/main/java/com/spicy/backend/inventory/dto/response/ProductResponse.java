package com.spicy.backend.inventory.dto.response;

import java.util.List;

public record ProductResponse(
        List<ProductSummaryResponse> products
) {

}
