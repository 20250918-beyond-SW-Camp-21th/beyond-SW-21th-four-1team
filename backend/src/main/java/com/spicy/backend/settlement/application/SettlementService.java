package com.spicy.backend.settlement.application;

import com.spicy.backend.settlement.dao.SettlementRepository; // Custom 대신 통합 Repository 주입
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service // 1. 서비스 빈 등록 필수
@RequiredArgsConstructor // 2. final 필드 생성자 주입
@Transactional(readOnly = true) // 3. 읽기 전용 트랜잭션 최적화
public class SettlementService {

    // SettlementRepository가 CustomRepository를 상속받고 있으므로 하나만 있으면 됩니다.
    private final SettlementRepository settlementRepository;

    public DailySettlementResponse getDailySettlement(DailySettlementRequest request) {
        // 특정 날짜 데이터 조회 (request.storeId -> request.storeId())
        Settlement daily = settlementRepository.findByStoreIdAndSettlementDate(request.storeId(), request.date())
                .orElse(null);

        // 월 누적 금액 계산
        LocalDate firstDay = request.date().withDayOfMonth(1);
        List<Settlement> monthlyList = settlementRepository.findByStoreIdAndSettlementDateBetween(
                request.storeId(), firstDay, request.date());

        // BigDecimal 정밀 연산
        BigDecimal accumulatedAmount = monthlyList.stream()
                .map(Settlement::getTotalOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DailySettlementResponse.builder()
                .orderCount(daily != null ? daily.getOrderCount() : 0)
                .dailyAmount(daily != null ? daily.getTotalOrderAmount() : BigDecimal.ZERO)
                .monthlyAccumulatedAmount(accumulatedAmount)
                .build();
    }

    public MonthlySettlementResponse getMonthlySettlement(MonthlySettlementRequest request) {
        YearMonth ym = YearMonth.parse(request.yearMonth());

        // sotreId() -> storeId() 오타 수정
        List<Settlement> monthlyList = settlementRepository.findByStoreIdAndSettlementDateBetween(
                request.storeId(), ym.atDay(1), ym.atEndOfMonth());

        BigDecimal totalOrder = monthlyList.stream().map(Settlement::getTotalOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalComm = monthlyList.stream().map(Settlement::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSettle = monthlyList.stream().map(Settlement::getSettlementAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return monthlyList.stream().findFirst()
                .map(s -> MonthlySettlementResponse.builder()
                        .totalAmount(totalOrder)
                        .commissionAmount(totalComm)
                        .settlementAmount(totalSettle)
                        .status(s.getStatus())
                        .payoutDate(s.getPayoutDate())
                        .build())
                .orElse(MonthlySettlementResponse.builder()
                        .totalAmount(BigDecimal.ZERO)
                        .commissionAmount(BigDecimal.ZERO)
                        .settlementAmount(BigDecimal.ZERO)
                        .build());
    }
}