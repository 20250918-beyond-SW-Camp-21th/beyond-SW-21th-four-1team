package com.spicy.backend.demandplan.service;

import com.spicy.backend.demandplan.controller.dto.response.ProcessResponse;
import com.spicy.backend.demandplan.controller.dto.response.StockResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class DemandPlanServiceTests {

    @Mock
    private ExternalDataFetcher infoProvider;

    @InjectMocks
    private DemandPlanService demandPlanService;

    @Test
    @DisplayName("재고가 충분한 경우")
    void shouldReturnEmptyString() {

        // given (준비)
        Long productId = 2L;
        StockResponseDto mockStock = new StockResponseDto(
                "삼각 오뎅",
                new BigDecimal("1200"),
                10,
                24,
                List.of()
        );

        // 재고 가져오도록 설정
        given(infoProvider.getStock(productId)).willReturn(mockStock);

        // when (실행)
        ProcessResponse processResponse = demandPlanService.process(productId);

        // then (검증)
        assertThat(processResponse.isOrderRequired()).isFalse();
        assertThat(processResponse.message()).isEmpty();
        System.out.println("결과 메시지 : " + processResponse.message());
    }

    @Test
    @DisplayName("현재 재고 수량이 안전 재고보다 적을 경우")
    void shouldProcessSuccessfully() {

        // given (준비)
        Long productId = 1L;
        StockResponseDto mockStock = new StockResponseDto(
                "밀떡",
                new BigDecimal("1000"),
                10,
                5,
                List.of()
        );

        // 재고 가져오도록 설정
        given(infoProvider.getStock(productId)).willReturn(mockStock);
        // 판매 내역 생성(10)
        given(infoProvider.getRecentOrderCount(productId, 1)).willReturn(79);

        // when (실행)
        ProcessResponse processResponse = demandPlanService.process(productId);

        // then (검증)
        assertThat(processResponse.isOrderRequired()).isTrue();
        assertThat(processResponse.message()).contains("추가 주문이 필요합니다.");
        System.out.println("결과 메시지 : " + processResponse.message());
    }

    @Test
    @DisplayName("최근 판매 내역이 비어있는 경우")
    void shouldVerifyIsEmptyAndReturnDefaultOrderCount() {

        // given (재고 부족 및 최근 판매 내역 비어 있도록 설정)
        Long productId = 1L;
        StockResponseDto stock = new StockResponseDto("밀떡", new BigDecimal("1000"), 10, 5, List.of());

        given(infoProvider.getStock(productId)).willReturn(stock);
        given(infoProvider.getRecentOrderCount(productId, 1)).willReturn(0);
        given(infoProvider.getRecentOrderCount(productId, 3)).willReturn(0);

        // when & then (실행 및 결과 확인)
        ProcessResponse processResponse = demandPlanService.process(productId);

        assertThat(processResponse.isOrderRequired()).isTrue();
        assertThat(processResponse.message()).contains("최근 주문이 없습니다.");
        System.out.println("결과 메시지: " + processResponse.message());
    }

}