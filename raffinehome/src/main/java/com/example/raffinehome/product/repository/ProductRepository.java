package com.example.raffinehome.product.repository;

import com.example.raffinehome.product.entity.Product;

import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - ?2 WHERE p.id = ?1 AND p.stockQuantity >= ?2")
    int decreaseStock(int id, int stockQuantity);


    // 追加　by K.K
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false")
    List<Product> findAllActiveProducts();

}