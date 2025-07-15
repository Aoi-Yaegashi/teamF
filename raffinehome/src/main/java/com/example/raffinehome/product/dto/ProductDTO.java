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
    public boolean isDeleted;
    public Object getIsDeleted() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getIsDeleted'");
    }
public ProductDTO(int id, String name, int price, int salePrice, String description,
                  int stockQuantity, String imageUrl, boolean isInStock) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.salePrice = salePrice;
    this.description = description;
    this.stockQuantity = stockQuantity;
    this.imageUrl = imageUrl;
    this.isInStock = isInStock;
    }
}