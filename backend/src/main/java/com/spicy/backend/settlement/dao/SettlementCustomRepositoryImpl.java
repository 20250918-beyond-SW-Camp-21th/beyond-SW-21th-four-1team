package com.spicy.backend.settlement.dao;

import com.spicy.backend.settlement.domain.Settlement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SettlementCustomRepositoryImpl implements SettlementCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    // SettlementCustomRepositoryImpl.java

    @Override
    public void bulkInsertSettlements(List<Settlement> settlements) {
        if (settlements == null || settlements.isEmpty()) return;

        // 1. SQL 문에 settlement_amount, commission_amount, total_order_amount를 반드시 포함해야 합니다.
        String sql = "INSERT INTO settlement (store_id, settlement_date, order_count, supply_amount, " +
                "tax_amount, total_settlement_amount, payout_date, status, product_id, pdf_url, " +
                "settlement_amount, commission_amount, total_order_amount, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        jdbcTemplate.batchUpdate(sql, settlements, 1000, (ps, settlement) -> {
            ps.setObject(1, settlement.getStoreId());
            ps.setDate(2, java.sql.Date.valueOf(settlement.getSettlementDate()));
            ps.setObject(3, settlement.getOrderCount());
            ps.setBigDecimal(4, settlement.getSupplyAmount());
            ps.setBigDecimal(5, settlement.getTaxAmount());
            ps.setBigDecimal(6, settlement.getTotalSettlementAmount());
            ps.setDate(7, settlement.getPayoutDate() != null ? java.sql.Date.valueOf(settlement.getPayoutDate()) : null);
            ps.setString(8, settlement.getStatus().name());
            ps.setObject(9, settlement.getProductId());
            ps.setString(10, settlement.getPdfUrl());
            // 2. 추가된 필드 매핑 (이 부분이 누락되면 500 에러가 발생합니다)
            ps.setBigDecimal(11, settlement.getSettlementAmount());
            ps.setBigDecimal(12, settlement.getCommissionAmount());
            ps.setBigDecimal(13, settlement.getTotalOrderAmount());
        });
    }
}