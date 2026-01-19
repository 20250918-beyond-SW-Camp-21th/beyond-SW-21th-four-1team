package com.spicy.backend.order.dao.order;

import com.spicy.backend.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {

    // 정산에서 주문ID로 아이템 조회할 때 사용
    List<OrderItem> findAllByOrderId(Long orderId);

    // 정산 기간조회에서 N+1 방지용 (추천)
    List<OrderItem> findAllByOrderIdIn(List<Long> orderIds);
}
