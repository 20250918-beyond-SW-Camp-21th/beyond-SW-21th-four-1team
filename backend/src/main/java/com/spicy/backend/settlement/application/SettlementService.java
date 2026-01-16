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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final OrderRepository orderRepository;

    public DailySettlementResponse getDailySettlement(DailySettlementRequest request) {
        // 특정 날짜 데이터 조회
        Settlement daily = settlementRepository.findByStoreIdAndSettlementDate(request.storeId(), request.date())
                .orElseThrow(() -> new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        // 월 누적 금액 계산
        LocalDate firstDay = request.date().withDayOfMonth(1);
        List<Settlement> monthlyList = settlementRepository.findByStoreIdAndSettlementDateBetween(
                request.storeId(), firstDay, request.date());

        // BigDecimal 정밀 연산
        BigDecimal accumulatedAmount = monthlyList.stream()
                .map(Settlement::getTotalOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DailySettlementResponse.builder()
                .orderCount(daily.getOrderCount())
                .dailyAmount(daily.getTotalOrderAmount())
                .monthlyAccumulatedAmount(accumulatedAmount)
                .productId(daily.getProductId())
                .build();
    }

    public MonthlySettlementResponse getMonthlySettlement(MonthlySettlementRequest request) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(request.yearMonth());
            } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 년월 형식입니다: " + request.yearMonth(), e);
            }


        List<Settlement> monthlyList = settlementRepository.findByStoreIdAndSettlementDateBetween(
                request.storeId(), ym.atDay(1), ym.atEndOfMonth());

        BigDecimal totalOrder = monthlyList.stream().map(Settlement::getTotalOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalComm = monthlyList.stream().map(Settlement::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSettle = monthlyList.stream().map(Settlement::getSettlementAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return monthlyList.stream()
                .max(Comparator.comparing(Settlement::getSettlementDate))
                .map(s -> MonthlySettlementResponse.builder()
                        .totalAmount(totalOrder)
                        .commissionAmount(totalComm)
                        .settlementAmount(totalSettle)
                        .status(s.getStatus())
                        .payoutDate(s.getPayoutDate())
                        .productId(s.getProductId())
                        .build())
                .orElse(MonthlySettlementResponse.builder()
                        .totalAmount(BigDecimal.ZERO)
                        .commissionAmount(BigDecimal.ZERO)
                        .settlementAmount(BigDecimal.ZERO)
                        .status(null)
                        .payoutDate(null)
                        .productId(null)
                        .build());
    }

    @Transactional
    public void createSettlement(Long storeId, Long productId, LocalDate targetDate) {

        // 중복 체크
        if (settlementRepository.findByStoreIdAndSettlementDate(storeId, targetDate).isPresent()) {
            throw new BusinessException(SettlementErrorCode.SETTLEMENT_ALREADY_EXISTS);
        }

        java.time.LocalDateTime startOfDay = targetDate.atStartOfDay();
        java.time.LocalDateTime endOfDay = targetDate.atTime(java.time.LocalTime.MAX);

        List<Order> orders = orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
                storeId,
                com.spicy.backend.order.enums.Status.DELIVERED, // 또는 상황에 맞는 '완료' 상태
                startOfDay,
                endOfDay
        );

        int count = orders.size();

        BigDecimal totalAmount = orders.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal commission = totalAmount.multiply(new BigDecimal("0.05")); // 예: 수수료 5%
        BigDecimal settleAmount = totalAmount.subtract(commission);

        Settlement settlement = Settlement.builder()
                .storeId(storeId)
                .settlementDate(targetDate)
                .totalOrderAmount(totalAmount)
                .commissionAmount(commission)
                .settlementAmount(settleAmount)
                .orderCount(count)
                .status(SettlementStatus.WAITING)
                .productId(productId)
                .build();

        settlementRepository.save(settlement);
    }


    public Integer getOrderCountInTerm(Long productId, int term) {

        if(productId == null) {
            throw new BusinessException(DemandPlanErrorCode.FAILED_TO_FETCH_PRODUCTS);
        }
        if(term < 0) {
            throw new BusinessException(DemandPlanErrorCode.NOT_VALID_TERM);
        }

        LocalDate  endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(term);

        return settlementRepository.getTotalQuantity(productId, startDate, endDate);
    }
}