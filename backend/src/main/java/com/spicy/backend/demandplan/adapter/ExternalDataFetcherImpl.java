package com.spicy.backend.demandplan.adapter;

import com.spicy.backend.demandplan.controller.dto.response.StockResponseDto;
import com.spicy.backend.demandplan.error.DemandPlanErrorCode;
import com.spicy.backend.demandplan.service.ExternalDataFetcher;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.inventory.application.InventoryService;
import com.spicy.backend.settlement.application.SettlementService;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public List<Integer> getRecentOrderCount(Long productId, int month) {
        List<Integer> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = RECENT_DAYS * month - 1; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);

            try {
                // 요청
                DailySettlementRequest request = new DailySettlementRequest(productId, targetDate);

                // 주문 내역 확인
                DailySettlementResponse response = settlementService.getDailySettlement(request);

                // 리스트에 add
                result.add(response.orderCount());

            } catch (BusinessException e) {
                // 해당 일자에 데이터 조회 실패시 0으로 간주
                result.add(0);
            }
        }

        return result;
    }
}
