package com.spicy.backend.settlement.dao;

import com.spicy.backend.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {
    Optional<Settlement> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);
    List<Settlement> findByStoreIdAndSettlementDateBetween(Long storeId, LocalDate start, LocalDate end);

    // 일정 기간 동안의 총 주문 수량
    @Query("SELECT SUM(s.orderCount) FROM Settlement s " +
            "WHERE s.productId = :productId " +
            "AND s.settlementDate BETWEEN :startDate AND :endDate")
    Integer getTotalQuantity(Long productId, LocalDate startDate, LocalDate endDate);
}