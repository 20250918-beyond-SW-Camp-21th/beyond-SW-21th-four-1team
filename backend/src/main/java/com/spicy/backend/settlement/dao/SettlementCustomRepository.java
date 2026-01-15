package com.spicy.backend.settlement.dao;

import com.spicy.backend.settlement.domain.Settlement;

import java.time.LocalDate;
import java.util.List;

public interface SettlementCustomRepository {
    // Bulk Insert를 위한 커스텀 인터페이스
    void bulkInsertSettlements(List<Settlement> settlements);

}