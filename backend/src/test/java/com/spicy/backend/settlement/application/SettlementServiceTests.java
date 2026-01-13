package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dao.SettlementRepository;
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTests {

    @InjectMocks
    private SettlementService settlementService;

    @Mock
    private SettlementRepository settlementRepository;

    @Test
    @DisplayName("일별 정산 조회 시, 해당 일자 데이터와 월 누적 합계가 정확히 계산되어야 한다")
    void getDailySettlement_Success() {
        // given
        Long storeId = 1L;
        LocalDate date = LocalDate.of(2026, 1, 10);
        DailySettlementRequest request = new DailySettlementRequest(storeId, date);

        Settlement daily = createSettlement(date, "10000");
        List<Settlement> monthlyList = List.of(
                createSettlement(LocalDate.of(2026, 1, 1), "5000"),
                createSettlement(LocalDate.of(2026, 1, 10), "10000")
        );

        given(settlementRepository.findByStoreIdAndSettlementDate(storeId, date)).willReturn(Optional.of(daily));
        given(settlementRepository.findByStoreIdAndSettlementDateBetween(any(), any(), any())).willReturn(monthlyList);

        // when
        DailySettlementResponse response = settlementService.getDailySettlement(request);

        // then
        assertThat(response.dailyAmount()).isEqualByComparingTo("10000");
        assertThat(response.monthlyAccumulatedAmount()).isEqualByComparingTo("15000");
    }

    @Test
    @DisplayName("월별 정산 조회 시, 한 달 치 데이터가 모두 합산되어야 한다")
    void getMonthlySettlement_Success() {
        // given
        MonthlySettlementRequest request = new MonthlySettlementRequest(1L, "2026-01");
        List<Settlement> monthlyList = List.of(
                createSettlement(LocalDate.of(2026, 1, 1), "10000", "1000", "9000"),
                createSettlement(LocalDate.of(2026, 1, 31), "20000", "2000", "18000")
        );

        given(settlementRepository.findByStoreIdAndSettlementDateBetween(any(), any(), any())).willReturn(monthlyList);

        // when
        MonthlySettlementResponse response = settlementService.getMonthlySettlement(request);

        // then
        assertThat(response.totalAmount()).isEqualByComparingTo("30000");
        assertThat(response.commissionAmount()).isEqualByComparingTo("3000");
        assertThat(response.settlementAmount()).isEqualByComparingTo("27000");
    }

    @Test
    @DisplayName("데이터가 없는 달을 조회하면 모든 합계가 0으로 반환되어야 한다")
    void getMonthlySettlement_Empty() {
        // given
        given(settlementRepository.findByStoreIdAndSettlementDateBetween(any(), any(), any())).willReturn(Collections.emptyList());

        // when
        MonthlySettlementResponse response = settlementService.getMonthlySettlement(new MonthlySettlementRequest(1L, "2026-01"));

        // then
        assertThat(response.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.status()).isNull();
    }

    private Settlement createSettlement(LocalDate date, String amount) {
        return createSettlement(date, amount, "0", amount);
    }

    private Settlement createSettlement(LocalDate date, String total, String comm, String settle) {
        return Settlement.builder()
                .settlementDate(date)
                .totalOrderAmount(new BigDecimal(total))
                .commissionAmount(new BigDecimal(comm))
                .settlementAmount(new BigDecimal(settle))
                .orderCount(1)
                .status(SettlementStatus.WAITING)
                .build();
    }
}