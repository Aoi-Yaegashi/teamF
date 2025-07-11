package com.example.raffinehome.order.service;

import com.example.raffinehome.cart.dto.Cart;
import com.example.raffinehome.cart.dto.CartItem;
<<<<<<< HEAD
import com.example.raffinehome.order.entity.Order;
import com.example.raffinehome.order.entity.OrderItem;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.order.repository.OrderItemRepository;
=======
import com.example.raffinehome.order.dto.OrderItemDTO;
import com.example.raffinehome.order.dto.OrderDTO;
import com.example.raffinehome.order.dto.OrederCreateDTO;
import com.example.raffinehome.order.entity.Order;
import com.example.raffinehome.order.entity.OrderDetail;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.order.repository.OrderDetailRepository;
>>>>>>> 8418ac7618e70d499c60e7777592c7b835a4ac76
import com.example.raffinehome.order.repository.OrderRepository;
import com.example.raffinehome.product.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final Cart cartService;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            Cart cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public OrderDTO placeOrder(Cart cart, OrederCreateDTO orederCreateDTO, HttpSession session) {
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // 在庫確認
        for (CartItem cartItem : cart.getItems().values()) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isEmpty() || productOpt.get().getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("在庫不足または商品未存在: " + cartItem.getProductName());
            }
        }

        // 注文エンティティ作成
        Order order = new Order();
     OrederCreateDTO customerInfo = orederCreateDTO.getcustomerInfo();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setShippingAddress(customerInfo.getAddress());
        order.setPostalCode(customerInfo.getPostalCode());
        order.setOrderStatus("PENDING");

        // 注文明細作成と在庫減算
        for (CartItem cartItem : cart.getItems().values()) {
            Product product = productRepository.findById(cartItem.getProductId()).orElseThrow(
                () -> new IllegalStateException("在庫確認後に商品が見つかりません: " + cartItem.getProductName())
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.calculateSubtotal(); // 小計計算
            orderItem.setCreatedAt(LocalDateTime.now());

            order.addOrderItem(orderItem);

            // 在庫減算
            product.reduceStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        // 注文保存
        Order savedOrder = orderRepository.save(order);

        // カートクリア
        cartService.clearCart(HttpSession session);

<<<<<<< HEAD
        return new OrderResponse(savedOrder.getId(), savedOrder.getOrderDate());
=======
        return new OrderDTO(savedOrder.getOrderId(), savedOrder.getOrderDate());
>>>>>>> 8418ac7618e70d499c60e7777592c7b835a4ac76
    }
}
