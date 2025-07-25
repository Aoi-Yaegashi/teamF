package com.example.raffinehome.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
    private int id;
    private String name;
    private int price;
    private int salePrice;
    private String description;
    private int stockQuantity;
    private String imageUrl;
    private boolean isInStock;
    private boolean isDeleted;

public ProductDTO(int id, String name, int price, int salePrice, String description,
                  int stockQuantity, String imageUrl, boolean isInStock, boolean isDeleted) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.salePrice = salePrice;
    this.description = description;
    this.stockQuantity = stockQuantity;
    this.imageUrl = imageUrl;
    this.isInStock = isInStock;
    this.isDeleted = isDeleted;
    }
}