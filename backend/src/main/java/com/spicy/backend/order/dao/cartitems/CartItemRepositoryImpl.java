package com.spicy.backend.order.dao.cartitems;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spicy.backend.order.domain.CartItem;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.spicy.backend.order.domain.QCartItem.cartItem;
import static com.spicy.backend.order.domain.QProduct.product;

@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItem> findAllByUserIdAndStoreIdAndDeletedAtIsNull(Long userId, Long storeId) {
        return queryFactory
                .selectFrom(cartItem)
                .join(cartItem.product, product).fetchJoin()
                .where(
                        cartItem.userId.eq(userId),
                        cartItem.storeId.eq(storeId),
                        cartItem.deletedAt.isNull()
                )
                .fetch();
    }
}
