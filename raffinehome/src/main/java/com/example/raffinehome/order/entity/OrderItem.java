package com.example.raffinehome.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // orderDetailId → id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer productId; // Productエンティティ参照ではなくproduct_id

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer unitPrice; // price → unitPrice

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer subtotal;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculateSubtotal();
    }

    // 小計計算
    public void calculateSubtotal() {
        this.subtotal = (unitPrice != null && quantity != null) ? unitPrice * quantity : 0;
    }

    // バリデーション例
    public boolean validateQuantity() {
        return quantity != null && quantity > 0;
    }
}