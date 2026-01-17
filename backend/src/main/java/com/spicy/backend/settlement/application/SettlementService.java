package com.spicy.backend.settlement.application;

import com.spicy.backend.demandplan.error.DemandPlanErrorCode;
import com.spicy.backend.global.error.exception.BusinessException;
import com.spicy.backend.order.dao.order.OrderRepository;
import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.dto.response.OrderResponse;
import com.spicy.backend.settlement.dao.SettlementRepository;
import com.spicy.backend.settlement.domain.Settlement;
import com.spicy.backend.settlement.dto.request.DailySettlementRequest;
import com.spicy.backend.settlement.dto.request.MonthlySettlementRequest;
import com.spicy.backend.settlement.dto.response.DailySettlementResponse;
import com.spicy.backend.settlement.dto.response.MonthlySettlementResponse;
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
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final OrderRepository orderRepository;
    private final SettlementFileService settlementFileService; // 로컬 파일 서비스 의존성 추가

    /**
     * 일별 매입 내역 조회 (UI 대시보드 상단 위젯용)
     */
    public DailySettlementResponse getDailySettlement(DailySettlementRequest request) {
        Settlement daily = settlementRepository.findByStoreIdAndSettlementDate(request.storeId(), request.date())
                .orElseThrow(() -> new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        LocalDate firstDay = request.date().withDayOfMonth(1);
        List<Settlement> monthlyList = settlementRepository.findByStoreIdAndSettlementDateBetween(
                request.storeId(), firstDay, request.date());

        BigDecimal accumulatedAmount = monthlyList.stream()
                .map(Settlement::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DailySettlementResponse.builder()
                .orderCount(daily.getOrderCount())
                .dailyAmount(daily.getTotalSettlementAmount())
                .monthlyAccumulatedAmount(accumulatedAmount)
                .productId(daily.getProductId())
                .build();
    }

    /**
     * 월별 매입 정산 조회
     */
    public MonthlySettlementResponse getMonthlySettlement(MonthlySettlementRequest request) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(request.yearMonth());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 년월 형식입니다: " + request.yearMonth(), e);
        }

        List<Settlement> monthlyList = settlementRepository.findByStoreIdAndSettlementDateBetween(
                request.storeId(), ym.atDay(1), ym.atEndOfMonth());

        BigDecimal totalOrder = monthlyList.stream().map(Settlement::getTotalSettlementAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSupply = monthlyList.stream().map(Settlement::getSupplyAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = monthlyList.stream().map(Settlement::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return monthlyList.stream()
                .max(Comparator.comparing(Settlement::getSettlementDate))
                .map(s -> MonthlySettlementResponse.builder()
                        .totalAmount(totalOrder)
                        .supplyAmount(totalSupply)
                        .taxAmount(totalTax)
                        .status(s.getStatus())
                        .payoutDate(s.getPayoutDate())
                        .productId(s.getProductId())
                        .build())
                .orElse(MonthlySettlementResponse.builder()
                        .totalAmount(BigDecimal.ZERO)
                        .supplyAmount(BigDecimal.ZERO)
                        .taxAmount(BigDecimal.ZERO)
                        .status(null)
                        .payoutDate(null)
                        .productId(null)
                        .build());
    }

    /**
     * 정산 데이터 생성 및 영수증 PDF 로컬 저장 자동화
     */
    @Transactional
    public void createSettlement(Long storeId, Long productId, LocalDate targetDate) {
        if (settlementRepository.findByStoreIdAndSettlementDate(storeId, targetDate).isPresent()) {
            throw new BusinessException(SettlementErrorCode.SETTLEMENT_ALREADY_EXISTS);
        }

        List<Order> orders = orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
                storeId, com.spicy.backend.order.enums.Status.DELIVERED,
                targetDate.atStartOfDay(), targetDate.atTime(LocalTime.MAX));

        if (orders.isEmpty()) throw new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND);

        BigDecimal totalAmount = orders.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal divisor = new BigDecimal("1.1");
        BigDecimal supplyAmount = totalAmount.divide(divisor, 0, RoundingMode.HALF_UP);
        BigDecimal taxAmount = totalAmount.subtract(supplyAmount);

        Settlement settlement = Settlement.builder()
                .storeId(storeId).settlementDate(targetDate).totalSettlementAmount(totalAmount)
                .supplyAmount(supplyAmount).taxAmount(taxAmount).orderCount(orders.size())
                .status(SettlementStatus.ORDERED).productId(productId).build();

        settlementRepository.save(settlement);

        // PDF 생성 및 경로 업데이트 (맞물림 수정)
        DailySettlementResponse pdfData = DailySettlementResponse.builder()
                .productId(productId).orderCount(orders.size()).dailyAmount(totalAmount).build();

        String savedLocalPath = settlementFileService.saveDailySettlementPdf(pdfData, targetDate);
        settlement.updatePdfUrl(savedLocalPath);
    }

    /**
     * [추가] 정산 내역 목록 조회 (컨트롤러와 맞물림)
     */
    public List<DailySettlementResponse> getSettlementList(Long storeId) {
        List<Settlement> settlements = settlementRepository.findAll(); // 필요시 storeId 조건 추가
        return settlements.stream()
                .map(s -> DailySettlementResponse.builder()
                        .productId(s.getProductId())
                        .orderCount(s.getOrderCount())
                        .dailyAmount(s.getTotalSettlementAmount())
                        .build())
                .toList();
    }

    /**
     * 상세 팝업용: 정산에 포함된 개별 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersBySettlementDate(Long storeId, LocalDate date) {
        List<Order> orders = orderRepository.findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
                storeId,
                com.spicy.backend.order.enums.Status.DELIVERED,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );

        return OrderResponse.from(orders);
    }

    /**
     * 통계 그래프용: 기간별 매입 정산 데이터 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<DailySettlementResponse> getSettlementStats(Long storeId, LocalDate start, LocalDate end) {
        List<Settlement> settlements = settlementRepository.findByStoreIdAndSettlementDateBetween(storeId, start, end);

        return settlements.stream()
                .map(s -> DailySettlementResponse.builder()
                        .productId(s.getProductId())
                        .orderCount(s.getOrderCount())
                        .dailyAmount(s.getTotalSettlementAmount())
                        .build())
                .toList();
    }

    /**
     * [신규] 로컬 PDF 파일을 Resource로 로드 (컨트롤러의 파일 스트리밍 지원)
     */
    public Resource getPdfFileAsResource(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        String pdfUrl = settlement.getPdfUrl();
        if (pdfUrl == null || pdfUrl.isEmpty()) {
            throw new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND); // 혹은 전용 에러코드 사용
        }

        try {
            Path filePath = Paths.get(settlement.getPdfUrl()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) return resource;
            else throw new BusinessException(SettlementErrorCode.SETTLEMENT_NOT_FOUND);

        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 형식이 올바르지 않습니다.", e);
        }
    }

    /**
     * 수요 예측용 발주 수량 집계
     */
    public Integer getOrderCountInTerm(Long productId, int term) {
        if(productId == null) throw new BusinessException(DemandPlanErrorCode.FAILED_TO_FETCH_PRODUCTS);
        if(term < 0) throw new BusinessException(DemandPlanErrorCode.NOT_VALID_TERM);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(term);

        return settlementRepository.getTotalQuantity(productId, startDate, endDate);
    }
}