package com.spicy.backend.settlement.dao;

import com.spicy.backend.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {

    Optional<Settlement> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);

    List<Settlement> findByStoreIdAndSettlementDateBetween(Long storeId, LocalDate start, LocalDate end);

    // 가맹점별 전체 정산 목록 조회 (안티그래비티 UI 리스트용)
    List<Settlement> findAllByStoreIdOrderBySettlementDateDesc(Long storeId);

    // 일정 기간 동안 본사에 발주한 총 수량 집계 (정밀 연산 및 통계용)
    @Query("""
            SELECT SUM(s.orderCount) 
            FROM Settlement s 
            WHERE s.productId = :productId 
              AND s.settlementDate BETWEEN :startDate AND :endDate
           """)
    Integer getTotalQuantity(@Param("productId") Long productId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);
}