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
    @Min(value = 1, message = "価格は1以上で入力してください")
    private int price;

    @Min(value = 1, message = "セール価格は1以上で入力してください")
    private int salePrice;
    
    @Min(value = 0, message = "数量は0以上で入力してください")
    private int stockQuantity;

    private String imageUrl;

}