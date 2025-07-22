package com.example.raffinehome.order.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
public class OrderItemDTO {
    @Valid
    @NotNull(message = "顧客情報は必須です")
    @JsonBackReference
    @ToString.Exclude
    private OrderCreateDTO customerInfo;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String postalCode;
    private String phoneNumber;
}