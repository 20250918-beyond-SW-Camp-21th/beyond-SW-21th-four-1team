package com.spicy.backend.settlement.dao;

import com.spicy.backend.settlement.domain.Settlement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SettlementCustomRepositoryImpl implements SettlementCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsertSettlements(List<Settlement> settlements) {
        if (settlements == null || settlements.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO settlement (store_id, settlement_date, order_count, total_order_amount, " +
                "commission_amount, settlement_amount, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        jdbcTemplate.batchUpdate(sql, settlements, 1000, (ps, settlement) -> {
            ps.setObject(1, settlement.getStoreId());
            ps.setDate(2, Date.valueOf(settlement.getSettlementDate()));
            ps.setObject(3, settlement.getOrderCount());
            ps.setBigDecimal(4, settlement.getTotalOrderAmount());
            ps.setBigDecimal(5, settlement.getCommissionAmount());
            ps.setBigDecimal(6, settlement.getSettlementAmount());
            ps.setString(7, settlement.getStatus().name());
        });
    }
}