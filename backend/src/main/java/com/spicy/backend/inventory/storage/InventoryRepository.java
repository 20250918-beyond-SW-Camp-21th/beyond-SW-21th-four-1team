package com.spicy.backend.inventory.storage;

import com.spicy.backend.inventory.domain.Inventory;
import com.spicy.backend.inventory.dto.response.InventoryLotResponse;
import com.spicy.backend.inventory.dto.response.ProductBaseInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    @Query("""
        select new com.spicy.backend.inventory.dto.response.InventoryLotResponse(
            i.productId,
            i.quantity,
            i.expirationDate,
            i.status,
            i.productCode
        )
        from Inventory i
    """)
    List<InventoryLotResponse> findAllProduct();

    @Query("""
        select new com.spicy.backend.inventory.dto.response.ProductBaseInfo(
            i.productId,
            i.productName,
            i.price,
            m.minimumQuantity
        )
        from Inventory i
        join MinimumProduct m on i.productId = m.productId
        group by i.productId, i.productName, i.price, m.minimumQuantity
    """)
    List<ProductBaseInfo> findAllProductBaseInfo();

    List<Inventory> findByProductId(Long id);

    List<Inventory> findByProductName(String name);

    Optional<Inventory> findFirstByProductIdOrderByIdAsc(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select i
    from Inventory i
    where i.productId = :productId
      and i.expirationDate >= :targetDate
      and i.status = 'ACTIVE'
    order by i.expirationDate asc
""")
    List<Inventory> findValidProductsWithLock(
            @Param("productId") Long productId,
            @Param("targetDate") LocalDate targetDate
    );

    @Query("select i from Inventory i where i.expirationDate < :today AND i.status = 'ACTIVE'")
    List<Inventory> findExpiredInventories(@Param("today")LocalDate today);
}
