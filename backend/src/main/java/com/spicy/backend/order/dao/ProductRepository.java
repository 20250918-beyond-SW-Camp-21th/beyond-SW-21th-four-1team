package com.spicy.backend.order.dao;

import com.spicy.backend.order.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
