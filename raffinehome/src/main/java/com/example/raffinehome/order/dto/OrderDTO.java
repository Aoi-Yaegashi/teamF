package com.example.raffinehome.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String postalCode;
    private List<OrderItemDTO> items;
    private Integer totalAmount;
    private String orderStatus;
    private LocalDateTime orderDate;
    private Boolean canCancel;
    private Boolean canShip;
}