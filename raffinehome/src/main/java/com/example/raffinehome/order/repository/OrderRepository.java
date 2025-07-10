package com.example.raffinehome.order.repository;

import com.example.raffinehome.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // クラス図にあるメソッド
    List<Order> findByCustomerEmail(String customerEmail);

    List<Order> findByOrderStatus(String orderStatus);

    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}