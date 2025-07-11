package com.example.raffinehome.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String postalCode;
    private List<OrderItemCreateDTO> items;
}