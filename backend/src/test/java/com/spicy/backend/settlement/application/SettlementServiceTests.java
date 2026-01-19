package com.spicy.backend.settlement.application;

import com.spicy.backend.demandplan.error.DemandPlanErrorCode;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.order.domain.Order;
import com.spicy.backend.settlement.dao.SettlementRepository;
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import com.spicy.backend.settlement.error.SettlementErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTests {

    @InjectMocks
    private SettlementService settlementService;

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private OrderRepository orderRepository;

    // ✅ SettlementService 생성자 주입 대상이므로 Mock 필요
    @Mock
    private SettlementFileService settlementFileService;

    @Nested
    @DisplayName("일별 정산 조회 테스트")
    class GetDailySettlement {

        @Test
        @DisplayName("성공: 해당 일자의 정산 데이터와 월 누적 금액을 정확히 반환한다")
        void success() {
            // given
            Long storeId = 1L;
            LocalDate date = LocalDate.of(2026, 1, 10);
            DailySettlementRequest request = new DailySettlementRequest(storeId, 1L, date);

            Settlement daily = createSettlement(date, "10000");
            List<Settlement> monthlyList = List.of(
                    createSettlement(LocalDate.of(2026, 1, 1), "5000"),
                    daily
            );

            given(settlementRepository.findByStoreIdAndSettlementDate(storeId, date))
                    .willReturn(Optional.of(daily));
            given(settlementRepository.findByStoreIdAndSettlementDateBetween(eq(storeId), any(), eq(date)))
                    .willReturn(monthlyList);

            // when
            DailySettlementResponse response = settlementService.getDailySettlement(request);

            // then
            assertThat(response.dailyAmount()).isEqualByComparingTo("10000");
            assertThat(response.monthlyAccumulatedAmount()).isEqualByComparingTo("15000");
        }

        @Test
        @DisplayName("실패: 정산 내역이 없으면 BusinessException이 발생한다")
        void fail_NotFound() {
            // given
            given(settlementRepository.findByStoreIdAndSettlementDate(anyLong(), any()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    settlementService.getDailySettlement(new DailySettlementRequest(1L, 1L, LocalDate.now()))
            ).isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", SettlementErrorCode.SETTLEMENT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("월별 정산 조회 테스트")
    class GetMonthlySettlement {

        @Test
        @DisplayName("성공: 한 달간의 정산 데이터를 합산하여 반환한다")
        void success() {
            // given
            MonthlySettlementRequest request = new MonthlySettlementRequest(1L, 1L, "2026-01");
            List<Settlement> monthlyList = List.of(
                    createSettlement(LocalDate.of(2026, 1, 1), "10000"),
                    createSettlement(LocalDate.of(2026, 1, 15), "20000")
            );

            given(settlementRepository.findByStoreIdAndSettlementDateBetween(anyLong(), any(), any()))
                    .willReturn(monthlyList);

            // when
            MonthlySettlementResponse response = settlementService.getMonthlySettlement(request);

            // then
            assertThat(response.totalAmount()).isEqualByComparingTo("30000");

            // (원하면 아래도 검증 가능)
            // assertThat(response.supplyAmount()).isNotNull();
            // assertThat(response.taxAmount()).isNotNull();
        }

        @Test
        @DisplayName("실패: 잘못된 연월 형식인 경우 IllegalArgumentException이 발생한다")
        void fail_InvalidFormat() {
            // given
            MonthlySettlementRequest request = new MonthlySettlementRequest(1L, 1L, "202601");

            // when & then
            assertThatThrownBy(() -> settlementService.getMonthlySettlement(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("정산 생성 테스트")
    class CreateSettlement {

        @Test
        @DisplayName("성공: 배송 완료된 주문들을 수집하여 공급가/부가세를 계산해 정산을 생성한다")
        void success() {
            // given
            Long storeId = 1L;
            LocalDate date = LocalDate.of(2026, 1, 15);

            Order order1 = Order.builder().totalAmount(new BigDecimal("60000")).build();
            Order order2 = Order.builder().totalAmount(new BigDecimal("40000")).build();

            given(settlementRepository.findByStoreIdAndSettlementDate(anyLong(), any()))
                    .willReturn(Optional.empty());

            given(orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(anyLong(), any(), any(), any()))
                    .willReturn(List.of(order1, order2));

            // ✅ createSettlement 내부에서 호출하므로 stub 필요
            given(settlementFileService.saveDailySettlementPdf(any(), any()))
                    .willReturn("/tmp/test.pdf");

            // when
            settlementService.createSettlement(storeId, 100L, date);

            // then
            verify(settlementRepository, times(1)).save(argThat(settlement ->
                    settlement.getTotalSettlementAmount().compareTo(new BigDecimal("100000")) == 0
                            && settlement.getSupplyAmount().compareTo(new BigDecimal("90909")) == 0
                            && settlement.getTaxAmount().compareTo(new BigDecimal("9091")) == 0
                            && settlement.getStatus() == SettlementStatus.ORDERED
            ));
        }

        @Test
        @DisplayName("실패: 이미 정산이 존재하면 BusinessException이 발생한다")
        void fail_AlreadyExists() {
            // given
            given(settlementRepository.findByStoreIdAndSettlementDate(anyLong(), any()))
                    .willReturn(Optional.of(Settlement.builder().build()));

            // when & then
            assertThatThrownBy(() -> settlementService.createSettlement(1L, 100L, LocalDate.now()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", SettlementErrorCode.SETTLEMENT_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("예측용 주문 수량 집계 테스트")
    class GetOrderCountInTerm {

        @Test
        @DisplayName("성공: 기간 내의 총 수량을 반환한다")
        void success() {
            // given
            given(settlementRepository.getTotalQuantity(anyLong(), any(), any())).willReturn(150);

            // when
            Integer count = settlementService.getOrderCountInTerm(1L, 7);

            // then
            assertThat(count).isEqualTo(150);
        }

        @Test
        @DisplayName("실패: 기간이 음수이면 BusinessException이 발생한다")
        void fail_InvalidTerm() {
            // when & then
            assertThatThrownBy(() -> settlementService.getOrderCountInTerm(1L, -1))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DemandPlanErrorCode.NOT_VALID_TERM);
        }
    }

    // --- Helper Methods ---
    private Settlement createSettlement(LocalDate date, String totalAmount) {
        BigDecimal total = new BigDecimal(totalAmount);
        BigDecimal supply = total.divide(new BigDecimal("1.1"), 0, RoundingMode.HALF_UP);
        BigDecimal tax = total.subtract(supply);

        return Settlement.builder()
                .settlementDate(date)
                .totalSettlementAmount(total)
                .supplyAmount(supply)
                .taxAmount(tax)
                .orderCount(1)
                .status(SettlementStatus.ORDERED)
                .productId(1L)
                .build();
    }
}
