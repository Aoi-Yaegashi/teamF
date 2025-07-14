package com.example.raffinehome.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateDTO {
    @NotNull(message = "数量は必須です")
    @Min(value = 1, message = "数量は1以上である必要があります")
<<<<<<< HEAD
    private int quantity;
    private int productId;
=======
    private Integer quantity;
    private Integer productId;
>>>>>>> 739d32ff046cd5dce0f9b01bb67985579cc4a015
}