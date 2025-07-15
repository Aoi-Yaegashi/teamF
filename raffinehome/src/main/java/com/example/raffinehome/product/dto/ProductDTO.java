package com.example.raffinehome.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private int id;
    private String name;
    private int price;
    private int salePrice;
    private String description;
    private int stockQuantity;
    private String imageUrl;
    private boolean isInStock;
}