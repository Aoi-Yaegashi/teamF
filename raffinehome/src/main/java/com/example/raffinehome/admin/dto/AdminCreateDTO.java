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
public class AdminCreateDTO {
    @NotBlank(message = "商品名は必須です")
    private String name;
    @NotNull(message = "価格は必須です")
    @Min(value = 1, message = "価格は1以上で入力してください")
    private Integer price;
    @Min(value = 1, message = "salepriceは正しい値で入力してください")
    private Integer salePrice;
    private String description;
    @Min(value = 1, message = "stockQuantityは正しい値で入力してください")
    private int stockQuantity;
    private String imageUrl;
}