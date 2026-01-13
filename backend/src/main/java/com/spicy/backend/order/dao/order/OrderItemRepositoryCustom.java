package com.spicy.backend.order.dao.order;

import com.spicy.backend.order.domain.OrderItem;

import java.util.List;

public interface OrderItemRepositoryCustom {
    List<OrderItem> findAllByStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long storeId, Long orderId);
}
