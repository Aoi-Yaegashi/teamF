package com.example.raffinehome.admin.repository;

import com.example.raffinehome.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    
    @Modifying
    @Query("UPDATE Admin p SET p.stock = p.stock - ?2 WHERE p.adminId = ?1 AND p.stock >= ?2")
    int decreaseStock(Integer productId, Integer quantity);
}