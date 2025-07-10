package com.example.raffinehome.cart.controller;

import com.example.raffinehome.cart.dto.Cart;
import com.example.raffinehome.cart.dto.CartItem;
import com.example.raffinehome.cart.dto.CartUpdate;
import com.example.raffinehome.cart.dto.CartAdd;
import com.example.raffinehome.cart.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService; 
    
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    @GetMapping
    public ResponseEntity<Cart> getCart(HttpSession session) {
        Cart cart = cartService.getCartFromSession(session);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping
    public ResponseEntity<Cart> addItem(@Valid @RequestBody CartItem cartItemInfo, HttpSession session) {
        Cart cart = cartService.addItemToCart(
                cartItemInfo.getProductId(),
                cartItemInfo.getQuantity(),
                session
        );
        
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(cart);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateItem(
            @PathVariable String itemId,
            @Valid @RequestBody CartItemUpdateDTO quantityDto,
            HttpSession session) {
        Cart cart = cartService.updateItemQuantity(itemId, quantityDto.getQuantity(), session);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(@PathVariable String itemId, HttpSession session) {
        Cart cart = cartService.removeItemFromCart(itemId, session);
        return ResponseEntity.ok(cart);
    }
}