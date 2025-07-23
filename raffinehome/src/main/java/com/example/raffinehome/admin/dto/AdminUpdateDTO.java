package com.example.raffinehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateDTO{

    private int id;

    @NotBlank(message = "商品名は必須です")
    private String name;

    private String description;

    @NotNull(message = "商品価格は必須です")
    @Min(value = 1, message = "価格は1以上である必要があります")
    private Integer price;
    
    private int salePrice;
    private int stockQuantity;
    private String imageUrl;

}