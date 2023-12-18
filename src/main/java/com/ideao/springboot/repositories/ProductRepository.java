package com.ideao.springboot.repositories;

import com.ideao.springboot.models.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {
    @Query("""
            SELECT p FROM Product p
            WHERE (:name IS NULL OR p.name = :name)
            AND (:value IS NULL OR p.value = :value)
            """)
    Page<ProductModel> findAllWithFilters(String name, BigDecimal value, Pageable pageable);
}
