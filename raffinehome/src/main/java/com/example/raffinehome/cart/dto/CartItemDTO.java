package com.example.raffinehome.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {
    private String id;
    private int productId;
    private String name;
    private int price;
    private String imageUrl;
    private int quantity;
    private int subtotal;

}