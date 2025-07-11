package com.example.raffinehome.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDTO {
    private Integer productId;
    private Integer quantity;
}