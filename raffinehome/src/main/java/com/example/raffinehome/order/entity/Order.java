package com.example.raffinehome.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // クラス図に合わせてorderId→id

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String postalCode; // クラス図に合わせて追加

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(nullable = false)
    private String orderStatus; // クラス図に合わせてstatus→orderStatus

    @Column(nullable = false)
    private LocalDateTime orderDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 合計金額を計算
    public void calculateTotal() {
        totalAmount = items.stream()
                .mapToInt(OrderItem::getSubtotal)
                .sum();
    }

    // ステータス変更
    public void changeStatus(String status) {
        this.orderStatus = status;
    }

    // キャンセル可能判定
    public boolean canCancel() {
        return "NEW".equals(orderStatus) || "PENDING".equals(orderStatus);
    }

    // 発送可能判定
    public boolean canShip() {
        return "PAID".equals(orderStatus);
    }

    // バリデーション
    public void validateOrder() {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("注文商品がありません。");
        }
        // 他にも必要なバリデーションを追加
    }

    // OrderItem追加用ヘルパー
    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}