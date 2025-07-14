package com.example.raffinehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateDTO {
    private String name;
    private int price;
    private int salePrice;
    private String description;
    private int stockQuantity;
    private String imageUrl;
}