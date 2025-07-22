package com.example.raffinehome.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderCreateDTO {
    @NotBlank(message = "お名前は必須です")
    private String customerName;
    
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String customerEmail;
    
    @NotBlank(message = "住所は必須です")
    private String shippingAddress;
    
    @NotBlank(message = "電話番号は必須です")
    private String phoneNumber;
}