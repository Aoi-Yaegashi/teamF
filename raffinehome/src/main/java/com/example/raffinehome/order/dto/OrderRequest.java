package com.example.raffinehome.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @Valid
    @NotNull(message = "顧客情報は必須です")
    private CustomerInfo customerInfo;
}