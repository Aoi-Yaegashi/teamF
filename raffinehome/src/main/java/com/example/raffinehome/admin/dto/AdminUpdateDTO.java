package com.example.raffinehome.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateDTO{

    private int id;
    private String name;
    private String description;
    private int price;
    private int salePrice;
    private int stockQuantity;
    private String imageUrl;

}