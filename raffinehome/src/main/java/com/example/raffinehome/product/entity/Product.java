package com.example.raffinehome.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Integer price;

    public Integer getPrice() {
    return price;
    }

    public void setPrice(Integer price) {
    this.price = price;
    }

    @Column(nullable = false)
    private int salePrice;
    
    @Column(nullable = false)
    private int stockQuantity;
    public Integer getStockQuantity() {
    return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
    this.stockQuantity = stockQuantity;
    }

    @Column(nullable = false)
    private boolean isDeleted;
    
    private String imageUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}