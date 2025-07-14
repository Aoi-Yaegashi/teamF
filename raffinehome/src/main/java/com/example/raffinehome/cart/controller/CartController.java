package com.example.raffinehome.cart.controller;

import com.example.raffinehome.cart.dto.CartAddDTO;
import com.example.raffinehome.cart.dto.CartDTO;
import com.example.raffinehome.cart.dto.CartUpdateDTO;
import com.example.raffinehome.cart.service.CartService;
import jakarta.servlet.http.HttpSession;
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

    /**
     * カート情報の取得
     */
    @GetMapping
    public ResponseEntity<CartDTO> getCart(HttpSession session) {
<<<<<<< HEAD
        CartDTO cartDTO = cartService.getCart(session);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カートに商品を追加
     */
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(HttpSession session, @RequestBody CartAddDTO dto) {
        CartDTO cartDTO = cartService.addToCart(session, dto.getProduct_id(), dto.getQuantity());
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カートから商品を削除
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(HttpSession session, @PathVariable int productId) {
        CartDTO cartDTO = cartService.removeFromCart(session, productId);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カート内商品の数量を更新
     */
    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateCartItem(HttpSession session, @RequestBody CartUpdateDTO dto) {
        CartDTO cartDTO = cartService.updateCartItem(session, dto.getProduct_id(), dto.getQuantity());
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カートをクリア
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok().build();
    }

    /**
     * カート内商品点数を取得
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(HttpSession session) {
        int count = cartService.getCartItemCount(session);
        return ResponseEntity.ok(count);
    }

    /**
    %%いらなくね
     * カートの在庫検証（例：購入前チェックなど）
     */
    @GetMapping("/validate")
    public ResponseEntity<CartDTO> validateCart(HttpSession session) {
        cartService.validateCartStock(session);
        CartDTO cartDTO = cartService.getCart(session);
        return ResponseEntity.ok(cartDTO);
=======
        CartDTO cart = cartService.getCartFromSession(session);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping
    public ResponseEntity<CartDTO> addItem(@Valid @RequestBody CartItem cartItemInfo, HttpSession session) {
        CartDTO cart = cartService.addItemToCart(
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
    public ResponseEntity<CartDTO> updateItem(
            @PathVariable String itemId,
            @Valid @RequestBody CartUpdateDTO quantityDto,
            HttpSession session) {
        CartDTO cart = cartService.updateItemQuantity(itemId, quantityDto.getQuantity(), session);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeItem(@PathVariable String itemId, HttpSession session) {
        CartDTO cart = cartService.removeItemFromCart(itemId, session);
        return ResponseEntity.ok(cart);
>>>>>>> 72f40798637a00c51aabab0bcee175e8de86b140
    }
}