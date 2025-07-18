package com.example.raffinehome.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductDto {
    private int id;
    private String name;
    private String description;
    private Integer price; 
    private int salePrice;           
    private int stockQuantity; 
    private boolean isDeleted;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; 
}