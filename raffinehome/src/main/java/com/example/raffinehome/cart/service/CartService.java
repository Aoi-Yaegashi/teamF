package com.example.raffinehome.cart.service;

import com.example.raffinehome.cart.dto.CartDTO;
import com.example.raffinehome.cart.dto.CartItem;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";
    
    private final ProductRepository productRepository;
    
    @Autowired
    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public CartDTO getCartFromSession(HttpSession session) {
        CartDTO cart = (CartDTO) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new CartDTO();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
    
    public CartDTO addItemToCart(Integer productId, Integer quantity, HttpSession session) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            CartDTO cart = getCartFromSession(session);
            
            CartItem product = new CartItem();
            product.setProductId(product.getProductId());
            product.setName(product.getName());
            product.setPrice(product.getPrice());
            product.setImageUrl(product.getImageUrl());
            product.setQuantity(quantity);
            
            cart.addItem(product);
            session.setAttribute(CART_SESSION_KEY, cart);
            
            return cart;
        }
        
        return null;
    }
    
    public CartDTO updateItemQuantity(String itemId, Integer quantity, HttpSession session) {
        CartDTO cart = getCartFromSession(session);
        cart.updateQuantity(itemId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }
    
    public CartDTO removeItemFromCart(String itemId, HttpSession session) {
        CartDTO cart = getCartFromSession(session);
        cart.removeItem(itemId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }
    
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}