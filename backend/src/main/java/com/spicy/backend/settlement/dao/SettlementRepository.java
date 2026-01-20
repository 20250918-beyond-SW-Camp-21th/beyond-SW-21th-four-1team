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

    List<Settlement> findAllByStoreIdOrderBySettlementDateDesc(Long storeId);

    // productId가 null일 경우 가맹점 전체 수량을 집계하도록 변경
    @Query("""
            SELECT COALESCE(SUM(s.orderCount), 0) FROM Settlement s\s
                WHERE s.storeId = :storeId AND s.settlementDate BETWEEN :startDate AND :endDate
           """)
    Integer getTotalQuantity(@Param("storeId") Long storeId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);
}