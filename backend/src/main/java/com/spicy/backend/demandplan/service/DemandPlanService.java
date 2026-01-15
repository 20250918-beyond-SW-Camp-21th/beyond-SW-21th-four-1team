package com.spicy.backend.demandplan.service;

import com.spicy.backend.demandplan.controller.dto.response.ProcessResponse;
import com.spicy.backend.demandplan.controller.dto.response.StockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DemandPlanService {

    private final ExternalDataFetcher infoProvider;
    private static final double SAFETY_STOCK_RATE = 1.2;
    private static final int DEFAULT_MIN_ORDER_COUNT = 50;

    public ProcessResponse process(Long productId) {
        // 재고 및 유효 값 조회
        StockResponseDto stock = infoProvider.getStock(productId);

        // 유효성 판단 로직
        if(stock.totalQuantity() <= stock.minimumQuantity()) {
            // 추가 주문 권장 수량 메시지 출력
            String msg = "현재 재고는 " + stock.totalQuantity() +
                    "개 입니다. 추가 주문이 필요합니다. " + createRecommendationMessage(productId);
            return new ProcessResponse(true, msg);
        }
        return new ProcessResponse(false, "");
    }

    private String createRecommendationMessage(Long productId) {

        // 최근 한달 간 재고 파악 후 권장 수량 계산 로직
        Integer salesObj = infoProvider.getRecentOrderCount(productId, 1);
        int sales = (salesObj == null) ? 0 : Math.max(0, salesObj);

        // sales 리스트 null 체크 로직
        if(sales == 0) {
            // 기간을 늘려 재탐색
            Integer salesExtendedObj = infoProvider.getRecentOrderCount(productId, 3);
            sales = (salesExtendedObj == null) ? 0 : Math.max(0, salesExtendedObj);
        }

        // 추천 수량
        int recommended;
        //요약 메시지
        String prefix;

        // 최근 한달 간 재고 주문 수량이 0건인 경우 로직
        if(sales == 0) {
            recommended = DEFAULT_MIN_ORDER_COUNT;
            prefix = "최근 주문이 없습니다. ";
        } else {
            // 최근 한달의 총 매출과 안전 수량 배율 상수를 곱연산하여 계산(배율 조정 가능)
            recommended = (int) (sales * SAFETY_STOCK_RATE);
            prefix = "최근 주문 수량 기준 ";
        }
        return prefix + "추천 권장 수량은 " + recommended + "개 입니다.";
    }
}
