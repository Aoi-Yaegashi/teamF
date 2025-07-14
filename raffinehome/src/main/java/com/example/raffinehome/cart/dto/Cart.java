package com.example.raffinehome.cart.dto;

import lombok.Data;

import com.example.raffinehome.cart.dto.CartItem;
import com.example.raffinehome.product.entity.Product;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Cart implements Serializable {
    private Map<String, CartItem> items = new LinkedHashMap<>();
    private int totalQuantity;
    private int totalPrice;
    
    public void addItem(CartItem product) {
        String productId = String.valueOf(product.getProductId());
        
        // 既存のアイテムがあれば数量を加算
        if (items.containsKey(productId)) {
            CartItem quantity = items.get(productId);
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
            CartItem item = items.get(itemId);
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
        totalQuantity = 0;
        totalPrice = 0;
        
        for (CartItem item : items.values()) {
            totalQuantity += item.getQuantity();
            totalPrice += item.getSubtotal();
        }
    }
}