package com.example.raffinehome.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateDTO {
    @NotNull(message = "商品IDは必須です")
    private Integer productId;
    
    @NotNull(message = "数量は必須です")
    @Min(value = 1, message = "数量は1以上である必要があります")
    private Integer quantity;
}