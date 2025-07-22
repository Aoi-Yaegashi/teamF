package com.example.raffinehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateDTO {
    @NotBlank(message = "商品名は必須です")
    private String name;
    @Min(value = 1, message = "価格は1以上で入力してください")
    private int price;
    private int salePrice;
    private String description;
    private int stockQuantity;
    private String imageUrl;
}