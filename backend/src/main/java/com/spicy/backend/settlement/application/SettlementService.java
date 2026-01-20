package com.spicy.backend.settlement.application;

import com.spicy.backend.demandplan.error.DemandPlanErrorCode;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.order.OrderItemRepository;
import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.domain.OrderItem;
import com.spicy.backend.order.dto.response.OrderResponse;
import com.spicy.backend.settlement.dao.SettlementRepository;
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
import com.spicy.backend.settlement.dto.response.SettlementItemResponse;
import com.spicy.backend.settlement.enums.SettlementStatus;
import com.spicy.backend.settlement.error.SettlementErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SettlementFileService settlementFileService;

    /**
     * 일별 매입 내역 조회 (상세 품목 리스트 포함)
     */
    public DailySettlementResponse getDailySettlement(DailySettlementRequest request) {
        // 1. DB에 저장된 정산 마스터 레코드 조회
        Settlement daily = settlementRepository.findByStoreIdAndSettlementDate(request.storeId(), request.date())
                .orElseThrow(() -> new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        // 2. 해당 일자의 PENDING 주문 아이템들을 다시 수집
        List<SettlementItemResponse> items =
                getSettlementItemsInPeriod(request.storeId(), request.date(), request.date());

        BigDecimal monthlyAccumulatedAmount =
                calculateMonthlyAccumulatedAmount(request.storeId(), request.date());

        return DailySettlementResponse.builder()
                .items(items)
                .orderCount(daily.getOrderCount())
                .dailyAmount(daily.getTotalSettlementAmount())
                .monthlyAccumulatedAmount(monthlyAccumulatedAmount)
                .build();
    }

    /**
     * 월별 매입 정산 조회 (월간 전체 품목 상세 포함)
     */
    public MonthlySettlementResponse getMonthlySettlement(MonthlySettlementRequest request) {
        YearMonth ym = YearMonth.parse(request.yearMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Settlement> monthlyList =
                settlementRepository.findByStoreIdAndSettlementDateBetween(request.storeId(), start, end);

        List<SettlementItemResponse> items =
                getSettlementItemsInPeriod(request.storeId(), start, end);

        BigDecimal totalAmount = monthlyList.stream()
                .map(Settlement::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSupply = monthlyList.stream()
                .map(Settlement::getSupplyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = monthlyList.stream()
                .map(Settlement::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Settlement lastSettlement = monthlyList.stream()
                .max(Comparator.comparing(Settlement::getSettlementDate))
                .orElse(null);

        return MonthlySettlementResponse.builder()
                .items(items)
                .totalAmount(totalAmount)
                .supplyAmount(totalSupply)
                .taxAmount(totalTax)
                .status(lastSettlement != null ? lastSettlement.getStatus() : null)
                .payoutDate(lastSettlement != null ? lastSettlement.getPayoutDate() : null)
                .build();
    }

    /**
     * 정산 생성 및 PDF 저장 자동화
     */
    @Transactional
    public void createSettlement(Long storeId, LocalDate targetDate) {
        if (settlementRepository.findByStoreIdAndSettlementDate(storeId, targetDate).isPresent()) {
            throw new BusinessException(SettlementErrorCode.SETTLEMENT_ALREADY_EXISTS);
        }

        // 1. PENDING 상태 주문 조회
        List<Order> orders = orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
                storeId,
                com.spicy.backend.order.enums.Status.PENDING, // PENDING으로 변경
                targetDate.atStartOfDay(),
                targetDate.atTime(LocalTime.MAX));

        if (orders.isEmpty()) {
            throw new BusinessException(SettlementErrorCode.NO_ORDERS_FOR_SETTLEMENT);
        }

        // 2. OrderItem 전체 수집 및 금액 합산
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> allItems = orderItemRepository.findAllByOrderIdIn(orderIds);

        // 단가 * 수량 직접 계산
        BigDecimal totalAmount = allItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal supplyAmount = totalAmount.divide(new BigDecimal("1.1"), 0, RoundingMode.HALF_UP);
        BigDecimal taxAmount = totalAmount.subtract(supplyAmount);

        // 3. PDF용 DTO 생성
        List<SettlementItemResponse> itemResponses = allItems.stream()
                .map(item -> SettlementItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity().intValue())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        DailySettlementResponse pdfData = DailySettlementResponse.builder()
                .orderCount(orders.size())
                .dailyAmount(totalAmount)
                .items(itemResponses)
                .build();

        // 4. 저장
        String savedLocalPath = settlementFileService.saveDailySettlementPdf(pdfData, targetDate);

        Settlement settlement = Settlement.builder()
                .storeId(storeId)
                .settlementDate(targetDate)
                .orderCount(orders.size())
                .supplyAmount(supplyAmount)
                .taxAmount(taxAmount)
                .totalSettlementAmount(totalAmount)
                .totalOrderAmount(totalAmount)
                .settlementAmount(totalAmount)
                .commissionAmount(totalAmount.multiply(new BigDecimal("0.05")).setScale(0, RoundingMode.HALF_UP))
                .status(SettlementStatus.PENDING) // 정산 상태도 PENDING
                .pdfUrl(savedLocalPath)
                .productId(0L)
                .build();

        settlementRepository.save(settlement);
    }

    /**
     * [공통 로직] 특정 기간 내의 모든 배송완료 주문 아이템 수집 및 DTO 변환 (N+1 방지 버전)
     */
    private List<SettlementItemResponse> getSettlementItemsInPeriod(Long storeId, LocalDate start, LocalDate end) {
        List<Order> orders = orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
                storeId,
                com.spicy.backend.order.enums.Status.PENDING, // PENDING으로 수정
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX));

        if (orders.isEmpty()) return List.of();

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> allItems = orderItemRepository.findAllByOrderIdIn(orderIds);

        return allItems.stream()
                .map(item -> SettlementItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity().intValue())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();
    }

    /**
     * [공통 로직] 월 누적 매입 금액 계산
     */
    private BigDecimal calculateMonthlyAccumulatedAmount(Long storeId, LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        return settlementRepository.findByStoreIdAndSettlementDateBetween(storeId, firstDay, date).stream()
                .map(Settlement::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 정산 내역 요약 목록 조회
     */
    public List<DailySettlementResponse> getSettlementList(Long storeId) {
        return settlementRepository.findAllByStoreIdOrderBySettlementDateDesc(storeId).stream()
                .map(s -> DailySettlementResponse.builder()
                        .orderCount(s.getOrderCount())
                        .dailyAmount(s.getTotalSettlementAmount())
                        .build())
                .toList();
    }

    /**
     * 정산 포함 상세 주문 리스트 (팝업용)
     */
    public List<OrderResponse> getOrdersBySettlementDate(Long storeId, LocalDate date) {
        List<Order> orders = orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
                storeId, com.spicy.backend.order.enums.Status.DELIVERED,
                date.atStartOfDay(), date.atTime(LocalTime.MAX));

        return OrderResponse.from(orders);
    }

    /**
     * 그래프용 통계 데이터
     */
    public List<DailySettlementResponse> getSettlementStats(Long storeId, LocalDate start, LocalDate end) {
        return settlementRepository.findByStoreIdAndSettlementDateBetween(storeId, start, end).stream()
                .map(s -> DailySettlementResponse.builder()
                        .orderCount(s.getOrderCount())
                        .dailyAmount(s.getTotalSettlementAmount())
                        .build())
                .toList();
    }

    /**
     * 로컬 저장된 PDF 파일 로드
     */
    public Resource getPdfFileAsResource(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        try {
            Path filePath = Paths.get(settlement.getPdfUrl()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            else throw new BusinessException(SettlementErrorCode.FILE_NOT_FOUND);
        } catch (MalformedURLException e) {
            throw new RuntimeException("올바르지 않은 파일 경로입니다.", e);
        }
    }

    /**
     * 수요 예측용 집계 (현재 로직 유지)
     */
    public Integer getOrderCountInTerm(Long storeId, int term) {
        if (term < 0) {
            throw new BusinessException(DemandPlanErrorCode.NOT_VALID_TERM);
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(term);

        return settlementRepository.getTotalQuantity(storeId, startDate, endDate);
    }



}
