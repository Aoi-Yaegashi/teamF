package com.example.raffinehome.order.controller;

import com.example.raffinehome.cart.dto.Cart;
import com.example.raffinehome.order.dto.OrederCreateDTO;
import com.example.raffinehome.order.dto.OrderDTO;
import com.example.raffinehome.cart.service.CartService;
import com.example.raffinehome.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    
    @Autowired
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }
    
    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(
            @Valid @RequestBody OrederCreateDTO orederCreateDTO,
            HttpSession session) {
<<<<<<< HEAD
        
        Cart cart = cartService.getCartSession(session);
        
=======
System.out.println("controllerstart");
        Cart cart = cartService.getCartFromSession(session);
System.out.println("カート取得end");
>>>>>>> 7ab3de8bbab6de59b74c6750ad4e96182c805595
        if (cart == null || cart.getItems().isEmpty()) {
System.out.println("カートが空");            
            return ResponseEntity.badRequest().build();
        }
System.out.println("カートの中身あり");
        try {
            OrderDTO orderDTO = orderService.placeOrder(cart, orederCreateDTO, session);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
        } catch (Exception e) {
System.out.println("注文失敗");             
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}