package com.spicy.backend.settlement.dao;

import com.spicy.backend.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementCustomRepository {
    Optional<Settlement> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);
    List<Settlement> findByStoreIdAndSettlementDateBetween(Long storeId, LocalDate start, LocalDate end);
}