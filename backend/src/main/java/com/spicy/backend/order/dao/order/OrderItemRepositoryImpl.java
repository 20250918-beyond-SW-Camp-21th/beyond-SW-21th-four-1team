package com.spicy.backend.order.dao.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spicy.backend.order.domain.OrderItem;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.spicy.backend.order.domain.QOrder.order;
import static com.spicy.backend.order.domain.QOrderItem.orderItem;

@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderItem> findAllByStoreIdAndOrderIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long storeId, Long orderId) {
        return queryFactory
                .selectFrom(orderItem)
                .join(order).on(orderItem.orderId.eq(order.id))
                .where(
                        order.id.eq(orderId),
                        order.storeId.eq(storeId),
                        order.deletedAt.isNull()
                )
                .orderBy(order.createdAt.desc())
                .fetch();
    }
}
