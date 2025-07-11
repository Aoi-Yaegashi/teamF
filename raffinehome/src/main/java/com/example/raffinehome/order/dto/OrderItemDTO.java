package com.example.raffinehome.order.dto;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer unitPrice;
    private Integer quantity;
    private Integer subtotal;
=======
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDTO {
    @Valid
    @NotNull(message = "顧客情報は必須です")
    private OrederCreateDTO customerInfo;
>>>>>>> 8418ac7618e70d499c60e7777592c7b835a4ac76
}