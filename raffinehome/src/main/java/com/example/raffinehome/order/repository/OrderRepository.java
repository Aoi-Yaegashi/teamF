package com.example.raffinehome.order.repository;

import com.example.raffinehome.order.entity.Order;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByAdminUserIdOrderByOrderDateDesc(Integer adminUserId);

    List<Order> findByOrderDateBetweenOrderByOrderDateAsc(LocalDateTime startDate, LocalDateTime endDate);

    List<Order> findByOrderDateAfter(LocalDateTime dateTime);
    
}

