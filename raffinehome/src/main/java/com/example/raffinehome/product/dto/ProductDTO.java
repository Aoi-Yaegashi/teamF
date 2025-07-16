package com.example.raffinehome.product.dto;

<<<<<<< HEAD
public class ProductDTO {
    
}
=======
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
>>>>>>> c701c0a4d878665a65c66999082773ddbcd087f5
