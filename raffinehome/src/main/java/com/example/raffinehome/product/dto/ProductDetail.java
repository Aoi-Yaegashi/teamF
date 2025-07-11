package com.example.raffinehome.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {
    private int id;
    private String name;
    private int price;
    private int sale_price;
    private String description;
    private int stock_quantity;
    private String image_url;
}