package com.spicy.backend.demandplan.adapter;

import com.spicy.backend.demandplan.controller.dto.response.StockResponseDto;
import com.spicy.backend.demandplan.error.DemandPlanErrorCode;
import com.spicy.backend.demandplan.service.ExternalDataFetcher;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.inventory.application.InventoryService;
import com.spicy.backend.settlement.application.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalDataFetcherImpl implements ExternalDataFetcher {

    private final InventoryService inventoryService;
    private final SettlementService settlementService;

    // 최근 주문 내역 조회 기간
    private static final int RECENT_DAYS = 30;

    @Override
    public StockResponseDto getStock(Long productId) {

        var inventoryData = inventoryService.searchProduct(productId);

        if(inventoryData == null) {
            throw new BusinessException(DemandPlanErrorCode.FAILED_TO_FETCH_PRODUCTS);
        }

        return new StockResponseDto(
                inventoryData.productName(),
                inventoryData.price(),
                inventoryData.minimumQuantity(),
                inventoryData.totalQuantity(),
                inventoryData.products()
        );
    }

    @Override
    public Integer getRecentOrderCount(Long productId, int month) {
        // 기간 설정
        if(month <= 0 || month > 12) {
            throw new BusinessException(DemandPlanErrorCode.NOT_VALID_TERM);
        }
        int term = RECENT_DAYS * month - 1;
        return settlementService.getOrderCountInTerm(productId, term);
    }
}
