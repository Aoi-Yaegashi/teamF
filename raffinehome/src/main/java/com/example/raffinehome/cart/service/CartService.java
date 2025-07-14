package com.example.raffinehome.cart.service;

import com.example.raffinehome.cart.dto.CartDTO;
import com.example.raffinehome.cart.dto.CartItemDTO;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.exception.InsufficientStockException;
import com.example.raffinehome.product.exception.OutOfStockException;
import com.example.raffinehome.product.exception.ProductNotFoundException;
import com.example.raffinehome.product.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
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
            
            CartItemDTO item = new CartItemDTO();
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
        for (CartItemDTO item : cartSession.getItems().values()) {
            total += item.getQuantity();
        }
        return total;
    }
    public void clearCart(HttpSession session) {
    Cart cart = getCartSession(session);
    // clear()を使わずに全てのキーを削除
    if (cart.getItems() != null) {
        // キーのリストをコピーしてから削除（ConcurrentModificationException防止）
        List<String> keys = new ArrayList<>(cart.getItems().keySet());
        for (String key : keys) {
            cart.getItems().remove(key);
        }
    }
    }
    //うまくできてないかな
public void validateProductStock(int productId, int quantity) {
    Optional<Product> productOpt = productRepository.findById(productId);
    if (productOpt.isEmpty()) {
        throw new ProductNotFoundException(productId);
    }
    Product product = productOpt.get();
    // フィールドを直接参照（例: product.stockQuantity）
    if (product.getStock() <= 0) {
        throw new OutOfStockException(productId, product.getName());
    }
    if (product.getStock() < quantity) {
        throw new InsufficientStockException(productId, product.getName(), quantity, product.getStock());
    }
}
    public void validateCartStock(HttpSession session) {
        Cart cart = getCartSession(session);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return; // カートが空なら何もしない
        }
        for (CartItemDTO item : cart.getItems().values()) {
            // 商品IDと数量を取得
            int productId = item.getProductId();
            int quantity = item.getQuantity();
            // 商品をDBから取得
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                throw new ProductNotFoundException(productId);
            }
            Product product = productOpt.get();
            // 在庫チェック: 在庫が0以下なら在庫切れ
            if (product.getStock() <= 0) {
                throw new OutOfStockException(product.getProductId(), product.getName());
            }
            if (product.getStock() < quantity) {
                throw new InsufficientStockException(product.getProductId(), product.getName(), quantity, product.getStock());
            }
        }
    }

//refreshCartAvailabilityできてないです

public void saveCartSession(HttpSession session, Cart cart) {
    if (session == null || cart == null) {
        return;
    }
    session.setAttribute(CART_SESSION_KEY, cart);
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
}
