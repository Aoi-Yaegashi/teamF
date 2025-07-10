package com.example.raffinehome.order.controller;

import com.example.raffinehome.order.dto.OrderCreateDTO;
import com.example.raffinehome.order.dto.OrderDTO;
import com.example.raffinehome.order.dto.OrderStatusUpdateDTO;
import com.example.raffinehome.order.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 注文作成
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderCreateDTO orderCreateDTO,
            HttpSession session) {
        try {
            OrderDTO orderDTO = orderService.createOrder(orderCreateDTO, session);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
        } catch (Exception e) {
            // 例外ハンドラで詳細に制御するのが推奨
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 注文詳細取得
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Integer id) {
        try {
            OrderDTO orderDTO = orderService.getOrderById(id);
            return ResponseEntity.ok(orderDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 全注文一覧取得
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // 顧客メールアドレスで注文一覧取得
    @GetMapping("/customer")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(
            @RequestParam String email) {
        List<OrderDTO> orders = orderService.getOrdersByCustomer(email);
        return ResponseEntity.ok(orders);
    }

    // ステータスで注文一覧取得
    @GetMapping("/status")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @RequestParam String status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // 注文ステータス更新
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Integer id,
            @Valid @RequestBody OrderStatusUpdateDTO dto) {
        try {
            OrderDTO updated = orderService.updateOrderStatus(id, dto.getOrderStatus());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 注文キャンセル
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}