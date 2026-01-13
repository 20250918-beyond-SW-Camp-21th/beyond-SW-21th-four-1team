package com.spicy.backend.order.dao.cartitems;

import com.spicy.backend.order.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends
        JpaRepository<CartItem, Long>,
        CartItemRepositoryCustom
{
    Optional<CartItem> findByUserIdAndIdAndStoreIdAndDeletedAtIsNull(Long userId, Long cartId, Long storeId);
}
