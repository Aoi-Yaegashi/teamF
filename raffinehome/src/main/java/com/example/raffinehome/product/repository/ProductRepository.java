package com.example.raffinehome.product.repository;

import com.example.raffinehome.product.entity.Product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    @Modifying
    @Query(
        "UPDATE Product p SET p.stockQuantity = p.stockQuantity - ?2 WHERE p.id = ?1 AND p.stockQuantity >= ?2")
    int decreaseStock(int id, int stockQuantity);

    
}