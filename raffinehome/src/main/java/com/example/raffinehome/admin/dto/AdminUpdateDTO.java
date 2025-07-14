package com.example.raffinehome.admin.dto;

import lombok.Data;

@Data
public class AdminUpdateDTO{

    private String name;
    private String description;
    private int price;
    private int salePrice;
    private String imageUrl;
    private int stock;

}