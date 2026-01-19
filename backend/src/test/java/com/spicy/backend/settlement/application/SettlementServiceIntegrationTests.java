package com.spicy.backend.settlement.application;

import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.enums.Status;
import com.spicy.backend.settlement.dao.SettlementRepository;
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class SettlementServiceIntegrationTests {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("성공: 실제 주문 데이터를 기반으로 정산을 생성하고 DB에 저장한다")
    void createSettlement_IntegrationSuccess() {
        Long storeId = 99L;
        LocalDate targetDate = LocalDate.now();

        Order order = Order.builder()
                .storeId(storeId)
                .userId(1L)
                .totalAmount(new BigDecimal("100000"))
                .status(Status.DELIVERED)
                .orderNumber("ORD-2026-001")
                .receiverName("김윤경")
                .address("테스트 주소")
                .deliveryDate(LocalDate.now())
                .receiverPhone("010-1234-5678")
                .memo("테스트 메모")
                .build();

        orderRepository.save(order);

        // ✅ createSettlement(storeId, targetDate)로 변경
        settlementService.createSettlement(storeId, targetDate);

        Settlement saved = settlementRepository.findByStoreIdAndSettlementDate(storeId, targetDate)
                .orElseThrow();

        assertThat(saved.getTotalSettlementAmount()).isEqualByComparingTo("100000");
        assertThat(saved.getSupplyAmount()).isEqualByComparingTo("90909");
        assertThat(saved.getTaxAmount()).isEqualByComparingTo("9091");
        assertThat(saved.getStatus()).isEqualTo(SettlementStatus.ORDERED);
    }

    @Test
    @DisplayName("성공: 저장된 정산 데이터를 기반으로 일별/월별 조회가 통합적으로 작동한다")
    void getSettlement_IntegrationSuccess() {
        Long storeId = 88L;
        LocalDate date = LocalDate.of(2026, 1, 15);

        settlementRepository.save(createSettlementEntity(storeId, date, "10000"));
        settlementRepository.save(createSettlementEntity(storeId, date.minusDays(1), "20000"));

        // ✅ DailySettlementRequest(storeId, date)
        DailySettlementRequest request = new DailySettlementRequest(storeId, date);
        DailySettlementResponse response = settlementService.getDailySettlement(request);

        assertThat(response.dailyAmount()).isEqualByComparingTo("10000");
        assertThat(response.monthlyAccumulatedAmount()).isEqualByComparingTo("30000");
    }

    @Test
    @DisplayName("성공: 한 달간의 여러 정산 데이터를 합산하여 월별 응답을 생성한다")
    void getMonthlySettlement_IntegrationSuccess() {
        Long storeId = 77L;

        settlementRepository.save(createSettlementEntity(storeId, LocalDate.of(2026, 1, 5), "10000"));
        settlementRepository.save(createSettlementEntity(storeId, LocalDate.of(2026, 1, 20), "10000"));

        // ✅ MonthlySettlementRequest(storeId, "YYYY-MM")
        MonthlySettlementRequest request = new MonthlySettlementRequest(storeId, "2026-01");
        MonthlySettlementResponse response = settlementService.getMonthlySettlement(request);

        assertThat(response.totalAmount()).isEqualByComparingTo("20000");
    }

    private Settlement createSettlementEntity(Long storeId, LocalDate date, String totalAmount) {
        BigDecimal total = new BigDecimal(totalAmount);
        BigDecimal supply = total.divide(new BigDecimal("1.1"), 0, RoundingMode.HALF_UP);
        BigDecimal tax = total.subtract(supply);

        return Settlement.builder()
                .storeId(storeId)
                .settlementDate(date)
                .totalSettlementAmount(total)
                .settlementAmount(total)
                .totalOrderAmount(total)
                .supplyAmount(supply)
                .taxAmount(tax)
                .commissionAmount(new BigDecimal("100.00"))
                .orderCount(1)
                .status(SettlementStatus.ORDERED)
                .productId(0L) // ✅ 전체 정산이면 null 추천
                .build();
    }
}
