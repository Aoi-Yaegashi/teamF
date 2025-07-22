package com.example.raffinehome.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int id;

    private String customerName;
    
    private String customerEmail;
    
    @NotBlank(message = "出荷先住所は必須です")
    private String shippingAddress;
    
    private String postalCode;
    
    private String phoneNumber;
    
    private int subtotal;
    
    private int totalAmount;
    
    private String orderStatus;

    private LocalDateTime orderDate;

    private Boolean canCancel;
   
    private Boolean canShip;

    public OrderDTO(int id, LocalDateTime orderDate) {
    this.id = id;
    this.orderDate = orderDate;
}

}