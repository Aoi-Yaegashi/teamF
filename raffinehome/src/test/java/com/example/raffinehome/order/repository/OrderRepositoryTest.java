package com.example.raffinehome.order.repository;

import com.example.raffinehome.order.entity.Order;
import com.example.raffinehome.order.entity.OrderItem;
import com.example.raffinehome.product.entity.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class OrderRepositoryTest {

@Autowired
private OrderRepository orderRepository;
    
@Test
public void saveOrderWithDetails_Success() {
    // Order作成
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    order.setTotalAmount(3000);
    order.setCustomerName("テスト太郎");
    order.setCustomerEmail("test@example.com");
    order.setShippingAddress("東京都千代田区1-1");
    order.setPhoneNumber("090-1234-5678");
    order.setOrderStatus("処理中");

    // OrderItem作成1
    OrderItem item1 = new OrderItem();
    item1.setProductName("マグカップ");
    item1.setUnitPrice(1000);
    item1.setQuantity(2);
    item1.setSubtotal(2000);

    // OrderItem作成2
    OrderItem item2 = new OrderItem();
    item2.setProductName("タオル");
    item2.setUnitPrice(1000);
    item2.setQuantity(1);
    item2.setSubtotal(1000);

    // OrderにOrderItemを追加（双方向関連を設定）
    order.addOrderDetail(item1);
    order.addOrderDetail(item2);

    // 保存
    Order savedOrder = orderRepository.save(order);

    // 検証
    assertNotNull(savedOrder.getId());
    assertEquals(2, savedOrder.getOrderDetails().size());
    assertEquals("マグカップ", savedOrder.getOrderDetails().get(0).getProductName());
    assertEquals("タオル", savedOrder.getOrderDetails().get(1).getProductName());
}
}