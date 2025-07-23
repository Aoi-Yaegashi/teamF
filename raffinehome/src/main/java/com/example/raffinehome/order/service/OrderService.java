package com.example.raffinehome.order.service;

import com.example.raffinehome.cart.dto.CartItemDTO;

import com.example.raffinehome.cart.dto.CartDTO;

import com.example.raffinehome.cart.service.CartService;
import com.example.raffinehome.order.dto.OrderItemDTO;
import com.example.raffinehome.order.dto.OrderDTO;
import com.example.raffinehome.order.dto.OrderCreateDTO;
import com.example.raffinehome.order.entity.Order;
import com.example.raffinehome.order.entity.OrderItem;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.order.repository.OrderItemRepository;
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
    private final OrderItemRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderDetailRepository,
            ProductRepository productRepository,
            CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public OrderDTO placeOrder(CartDTO cart, OrderCreateDTO orderCreateDTO, HttpSession session) {
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // 在庫確認
        for (CartItemDTO cartItem : cart.getItems().values()) {

            if (cartItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("注文数は1以上でなければなりません: " + cartItem.getName());
            }

            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isEmpty() || productOpt.get().getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("在庫不足または商品未存在: " + cartItem.getName());
            }
        }

        // 注文エンティティ作成
        Order order = new Order();
        OrderCreateDTO customerInfo = orderCreateDTO.getCustomerInfo();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setCustomerName(orderCreateDTO.getCustomerName());
        order.setCustomerEmail(orderCreateDTO.getCustomerEmail());
        order.setShippingAddress(orderCreateDTO.getShippingAddress());
        order.setPhoneNumber(orderCreateDTO.getPhoneNumber());
        order.setOrderStatus("PENDING");

        // 注文明細作成と在庫減算
        for (CartItemDTO cartItem : cart.getItems().values()) {
            Product product = productRepository.findById(cartItem.getProductId()).orElseThrow(
                () -> new IllegalStateException("在庫確認後に商品が見つかりません: " + cartItem.getName())
            );

            OrderItem orderDetail = new OrderItem();
            orderDetail.setProduct(product);
            orderDetail.setProductName(product.getName());
            orderDetail.setUnitPrice(product.getPrice());
            orderDetail.setQuantity(cartItem.getQuantity());

            order.addOrderDetail(orderDetail);

            //  結合テスト用　エラー発生させるコード 
            //  在庫数が注文数以下
            //  product.getId() -100


            // 在庫減算処理と結果のチェック
            int updatedRows = productRepository.decreaseStock(product.getId(), cartItem.getQuantity());

            // 更新された行数が1でない場合（在庫更新に失敗した場合）
            if (updatedRows != 1) {
                throw new IllegalStateException(
                    "在庫の更新に失敗しました (更新行数: " + updatedRows + ")。" +
                    "商品ID: " + product.getId() +
                    ", 商品名: " + product.getName() +
                    ", 要求数量: " + cartItem.getQuantity()
                    // 必要であれば、考えられる原因（競合など）を示すメッセージを追加
                );
            }
        }



    //  結合テスト用　エラー発生させるコード  
    //　不正な顧客情報      
    //order.setCustomerName(null);


        // 注文保存
        Order savedOrder = orderRepository.save(order);

        // カートクリア

        //  結合テスト用　エラー発生させるコード
        //cartService.clearCart(null);

        cartService.clearCart(session);

        // OrderDTOへ必要なフィールドをセット
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(savedOrder.getId());
        orderDTO.setOrderDate(savedOrder.getOrderDate());
        orderDTO.setCustomerName(savedOrder.getCustomerName());
        orderDTO.setCustomerEmail(savedOrder.getCustomerEmail());
        orderDTO.setShippingAddress(savedOrder.getShippingAddress());
        orderDTO.setPhoneNumber(savedOrder.getPhoneNumber());
        orderDTO.setTotalAmount(savedOrder.getTotalAmount());
        orderDTO.setOrderStatus(savedOrder.getOrderStatus());
        
        int subtotal = 0;
        for (OrderItem item : savedOrder.getOrderDetails()) {
            subtotal += item.getUnitPrice() * item.getQuantity();
        }
        orderDTO.setSubtotal(subtotal);

        orderDTO.setCanCancel(true); 
        orderDTO.setCanShip(false);  

        return orderDTO;
    }
}