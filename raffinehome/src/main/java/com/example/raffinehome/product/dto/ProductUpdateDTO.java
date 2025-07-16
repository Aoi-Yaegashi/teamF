package com.example.raffinehome.product.dto;

<<<<<<< HEAD
public class ProductUpdateDTO {
    
}
=======
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    private String name;
    private int price;
    private int salePrice;
    private String description;
    private int stockQuantity;
    private String imageUrl;
}
>>>>>>> c701c0a4d878665a65c66999082773ddbcd087f5
