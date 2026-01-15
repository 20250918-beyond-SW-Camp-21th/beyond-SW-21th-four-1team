package com.spicy.backend.order.dao.order;

import com.spicy.backend.order.domain.Order;
import com.spicy.backend.order.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByStoreIdAndStatusOrderByCreatedAt(Long storeId, Status status);

    List<Order> findAllByStoreIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(Long storeId, Status status);

    Optional<Order> findByStoreIdAndIdAndDeletedAtIsNull(Long storeId, Long orderId);

    List<Order> findAllByStoreIdAndCreatedAtBetween(Long storeId, LocalDateTime start, LocalDateTime end);

    List<Order> findAllByStoreIdAndStatusAndCreatedAtBetweenAndDeletedAtIsNull(
            Long storeId,
            Status status, // Status.COMPLETED 등을 넘기기 위해 추가
            LocalDateTime start,
            LocalDateTime end,
            Long productId
    );
}
