package com.example.raffinehome.cart.dto;

import lombok.Data;

import com.example.raffinehome.cart.dto.CartItemDTO;
import com.example.raffinehome.product.entity.Product;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class CartDTO implements Serializable {
    private Map<String, CartItemDTO> items = new LinkedHashMap<>();
    private int itemCount;
    private int totalPrice;
    
    public void addItem(CartItemDTO product) {
        String productId = String.valueOf(product.getProductId());
        
        // 既存のアイテムがあれば数量を加算
        if (items.containsKey(productId)) {
            CartItemDTO quantity = items.get(productId);
            quantity.setQuantity(quantity.getQuantity() + product.getQuantity());
            quantity.setSubtotal(quantity.getPrice() * quantity.getQuantity());
        } else {
            // 新しいアイテムを追加
            product.setId(productId);
            product.setSubtotal(product.getPrice() * product.getQuantity());
            items.put(productId, product);
        }
        
        // 合計計算
        calculateTotals();
    }
    
    public void updateQuantity(String itemId, int quantity) {
        if (items.containsKey(itemId)) {
            CartItemDTO item = items.get(itemId);
            item.setQuantity(quantity);
            item.setSubtotal(item.getPrice() * quantity);
            calculateTotals();
        }
    }
    
    public void removeItem(String itemId) {
        items.remove(itemId);
        calculateTotals();
    }
    
    public void calculateTotals() {
        itemCount = 0;
        totalPrice = 0;
        
        for (CartItemDTO item : items.values()) {
            itemCount += item.getQuantity();
            totalPrice += item.getSubtotal();
        }
    }
}