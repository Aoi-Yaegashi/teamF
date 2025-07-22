package com.example.raffinehome.cart.controller;

import com.example.raffinehome.cart.dto.CartAddDTO;
import com.example.raffinehome.cart.dto.CartDTO;
import com.example.raffinehome.cart.dto.CartUpdateDTO;
import com.example.raffinehome.cart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


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
        CartDTO cartDTO = cartService.getCart(session);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カートに商品を追加
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(HttpSession session, @Valid @RequestBody CartAddDTO dto) {
        CartDTO cartDTO = cartService.addToCart(dto.getProductId(), dto.getQuantity(), session);

        if (cartDTO == null) {
            // 商品が見つからない、または追加できない場合
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "商品が見つかりません"));
        }
 
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カートから商品を削除
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(HttpSession session, @PathVariable int productId) {
        CartDTO cartDTO = cartService.removeFromCart(String.valueOf(productId), session);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カート内商品の数量を更新
     */
    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateCartItem(HttpSession session, @Valid @RequestBody CartUpdateDTO dto) {
        CartDTO cartDTO = cartService.updateCartItem(String.valueOf(dto.getProductId()), dto.getQuantity(), session);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * カートをクリア
     */
    @DeleteMapping("/clear")
public ResponseEntity<Void> clearCart(HttpSession session) {
    session.removeAttribute("cart"); // ← これで完全にサーバーのセッションから消える
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
    %%いりました
     * カートの在庫検証（例：購入前チェックなど）
     */
    @GetMapping("/validate")
    public ResponseEntity<CartDTO> validateCart(HttpSession session) {
        cartService.validateCartStock(session);
        CartDTO cartDTO = cartService.getCart(session);
        return ResponseEntity.ok(cartDTO);
    }
}