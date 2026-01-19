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
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest // 실제 컨테이너를 띄워 모든 빈과 DB를 연결
@Transactional  // 테스트 완료 후 DB 데이터를 롤백하여 깨끗하게 유지
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
        // given: 실제 DB에 주문 데이터 저장
        Long storeId = 99L;
        Long productId = 1L;
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
        // Reflection 등을 쓰지 않고 실제 생성 시간을 테스트 시간에 맞추기 위해
        // Repository의 save 시점에 생성 시간이 들어가는지 확인이 필요합니다.
        orderRepository.save(order);

        // when: 정산 생성 로직 실행
        settlementService.createSettlement(storeId, productId, targetDate);

        // then: 실제 DB에서 정산 데이터 조회 및 검증
        Settlement saved = settlementRepository.findByStoreIdAndSettlementDate(storeId, targetDate)
                .orElseThrow();

        assertThat(saved.getTotalOrderAmount()).isEqualByComparingTo("100000");
        assertThat(saved.getCommissionAmount()).isEqualByComparingTo("5000"); // 5%
        assertThat(saved.getSettlementAmount()).isEqualByComparingTo("95000");
    }

    @Test
    @DisplayName("성공: 저장된 정산 데이터를 기반으로 일별/월별 조회가 통합적으로 작동한다")
    void getSettlement_IntegrationSuccess() {
        // given: 테스트용 정산 데이터 DB에 직접 삽입
        Long storeId = 88L;
        LocalDate date = LocalDate.of(2026, 1, 15);

        Settlement s1 = createSettlementEntity(storeId, date, "10000");
        Settlement s2 = createSettlementEntity(storeId, date.minusDays(1), "20000");
        settlementRepository.save(s1);
        settlementRepository.save(s2);

        // when: 일별 조회 API 로직 호출
        DailySettlementRequest request = new DailySettlementRequest(storeId, 1L, date);
        DailySettlementResponse response = settlementService.getDailySettlement(request);

        // then: 누적 금액(10000 + 20000) 검증
        assertThat(response.dailyAmount()).isEqualByComparingTo("10000");
        assertThat(response.monthlyAccumulatedAmount()).isEqualByComparingTo("30000");
    }

    @Test
    @DisplayName("성공: 한 달간의 여러 정산 데이터를 합산하여 월별 응답을 생성한다")
    void getMonthlySettlement_IntegrationSuccess() {
        // given
        Long storeId = 77L;
        settlementRepository.save(createSettlementEntity(storeId, LocalDate.of(2026, 1, 5), "10000"));
        settlementRepository.save(createSettlementEntity(storeId, LocalDate.of(2026, 1, 20), "10000"));

        // when
        MonthlySettlementRequest request = new MonthlySettlementRequest(storeId, 1L, "2026-01");
        MonthlySettlementResponse response = settlementService.getMonthlySettlement(request);

        // then
        assertThat(response.totalAmount()).isEqualByComparingTo("20000");
    }

    // Helper: 엔티티 생성을 위한 편의 메서드
    private Settlement createSettlementEntity(Long storeId, LocalDate date, String amount) {
        BigDecimal total = new BigDecimal(amount);
        return Settlement.builder()
                .storeId(storeId)
                .settlementDate(date)
                .totalOrderAmount(total)
                .commissionAmount(total.multiply(new BigDecimal("0.05")))
                .settlementAmount(total.multiply(new BigDecimal("0.95")))
                .orderCount(1)
                .status(SettlementStatus.WAITING)
                .productId(1L)
                .build();
    }
}