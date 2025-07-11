package com.example.raffinehome.cart.service;

import com.example.raffinehome.cart.dto.Cart;
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
    
    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
    
    public Cart addToCart(Integer productId, Integer quantity, HttpSession session) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Cart cart = getCart(session);
            
            CartItem item = new CartItem();
            item.setProductId(product.getProductId());
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImageUrl());
            item.setQuantity(quantity);
            
            cart.addItem(item);
            session.setAttribute(CART_SESSION_KEY, cart);
            
            return cart;
        }
        
        return null;
    }

    public Cart removeFromCart(String productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }
    
    public Cart updateCartItem(String productId, Integer quantity, HttpSession session) {
        Cart cart = getCart(session);
        cart.updateQuantity(productId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    public int getCartItemCount(HttpSession session) {
        Cart cartSession = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cartSession == null || cartSession.getItems() == null) {
            return 0;
        }
        int total = 0;
        for (CartItem item : cartSession.getItems().values()) {
            total += item.getQuantity();
        }
        return total;
    }

        public Cart getCartSession(HttpSession session) {
        Cart cartSession = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cartSession == null) {
            cartSession = new Cart();
            // 必要に応じてsession_idやcreated_atなどを初期化
            session.setAttribute(CART_SESSION_KEY, cartSession);
        }
        return cartSession;
    }

        public void saveCartSession(HttpSession session, Cart cart) {
        if (session == null || cart == null) {
            return;
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}
