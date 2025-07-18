package com.example.raffinehome.order.repository;

import com.example.raffinehome.order.entity.Order;
import com.example.raffinehome.order.entity.OrderItem;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;

import jakarta.persistence.PersistenceException; // 制約違反用

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException; // Spring Data JPAの例外

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest // JPA関連のテストに特化した設定（インメモリDB使用、関連Beanのみロード）
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // テストデータ準備や永続化の検証に使用

    @Autowired
    private OrderRepository orderRepository; // テスト対象のリポジトリ

    @Autowired // OrderItemの削除確認用にインジェクト
    private OrderItemRepository orderItemRepository;

    @Autowired // テストデータ準備用にインジェクト
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    private Product createProduct(String name, int price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStockQuantity(100);
        return productRepository.save(p);
    }

    // 各テストメソッド実行前に共通の商品データを準備
    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setName("商品A");
        product1.setPrice(1000);
        product1.setStockQuantity(10);
        product1 = productRepository.save(product1);

        product2 = new Product();
        product2.setName("商品B");
        product2.setPrice(2000);
        product2.setStockQuantity(5);
        product2 = productRepository.save(product2);
    }

    // テスト用のOrderオブジェクトを作成するヘルパーメソッド
    private Order createSampleOrder(String customerName) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(3000); // (1000*1 + 2000*1)
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerName.toLowerCase() + "@example.com");
        order.setShippingAddress("住所 " + customerName);
        order.setPhoneNumber("090-" + customerName.hashCode());
        order.setOrderStatus("PENDING"); // 初期ステータス

        OrderItem detail1 = new OrderItem();
        detail1.setProduct(product1); // 事前に永続化したProductエンティティを設定
        detail1.setProductName(product1.getName());
        detail1.setUnitPrice(product1.getPrice());
        detail1.setQuantity(1);
        order.addOrderDetail(detail1); // Orderエンティティのヘルパーメソッドで詳細を追加

        OrderItem detail2 = new OrderItem();
        detail2.setProduct(product2);
        detail2.setProductName(product2.getName());
        detail2.setUnitPrice(product2.getPrice());
        detail2.setQuantity(1);
        order.addOrderDetail(detail2);
        return order;
    }

    @Test
    @DisplayName("注文と注文詳細を正常に保存できる")
    void saveOrderWithDetails_Success() {
        // Arrange
        Order order = createSampleOrder("顧客1");

        // Act
        Order savedOrder = orderRepository.save(order); // Orderを保存 (CascadeType.ALLによりOrderItemも保存されるはず)
        entityManager.flush(); // DBへ反映
        entityManager.clear(); // 永続化コンテキストキャッシュをクリアし、DBからの取得を確実にする

        // Assert
        // 保存されたOrderをDBから取得して検証
        Order foundOrder = entityManager.find(Order.class, savedOrder.getId());

        assertThat(foundOrder).isNotNull(); // Orderが取得できる
        assertThat(foundOrder.getId()).isNotNull(); // IDが払い出されている
        assertThat(foundOrder.getCustomerName()).isEqualTo(order.getCustomerName()); // 顧客名が正しい
        assertThat(foundOrder.getOrderDetails()).hasSize(2); // 注文詳細が2件含まれている
        // 注文詳細の内容も確認
        assertThat(foundOrder.getOrderDetails().get(0).getProductName()).isEqualTo(product1.getName());
        assertThat(foundOrder.getOrderDetails().get(0).getQuantity()).isEqualTo(1);
        assertThat(foundOrder.getOrderDetails().get(1).getProductName()).isEqualTo(product2.getName());
        assertThat(foundOrder.getOrderDetails().get(1).getQuantity()).isEqualTo(1);

        // 関連するOrderDetailも正しく永続化されていることを確認 (CascadeType.ALLの検証)
        OrderItem foundDetail1 = entityManager.find(OrderItem.class, foundOrder.getOrderDetails().get(0).getId());
        assertThat(foundDetail1).isNotNull();
        assertThat(foundDetail1.getOrder().getId()).isEqualTo(foundOrder.getId()); // Orderへの関連が設定されている
    }

    @Test
    @DisplayName("存在するIDで注文を検索でき、関連エンティティも取得できる")
    void findById_WhenOrderExists_ShouldReturnOrderWithDetails() {
        // Arrange
        Order order = createSampleOrder("検索用顧客");
        Order savedOrder = entityManager.persistFlushFind(order); // persist + flush + find を一括実行
        entityManager.clear();

        // Act
        Optional<Order> foundOrderOpt = orderRepository.findById(savedOrder.getId());

        // Assert
        assertThat(foundOrderOpt).isPresent();
        Order foundOrder = foundOrderOpt.get();

        assertThat(foundOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.getCustomerName()).isEqualTo(order.getCustomerName());

        // もし関連エンティティの取得もテストしたければ
        assertThat(foundOrder.getOrderDetails()).hasSize(2);
    }

    @Test
    @DisplayName("存在しないIDで注文を検索するとOptional.emptyが返る")
    void findById_WhenOrderNotExists_ShouldReturnEmpty() {
        // Arrange
        Integer nonExistingId = 999; // 存在しないであろうID

        // Act
        Optional<Order> foundOrderOpt = orderRepository.findById(nonExistingId);

        // Assert
        assertThat(foundOrderOpt).isNotPresent(); // Optionalが空であること
    }
    

    @Test
    @DisplayName("すべての注文を取得できる")
    void findAll_ShouldReturnAllOrders() {
        // Arrange
        Order order1 = createSampleOrder("全件顧客1");
        Order order2 = createSampleOrder("全件顧客2");
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Order> orders = orderRepository.findAll(); // 全件取得

        // Assert
        assertThat(orders).hasSize(2); // 2件取得できること
        // 顧客名などで内容を簡易的に確認
        assertThat(orders).extracting(Order::getCustomerName)
                         .containsExactlyInAnyOrder(order1.getCustomerName(), order2.getCustomerName());
    }

    @Test
    @DisplayName("注文日より後の注文を取得できること")
    void testFindByOrderDateAfter() {
       // Arrange
       LocalDateTime now = LocalDateTime.now();

       Order recentOrder = new Order();
       recentOrder.setOrderDate(now.plusDays(1)); // 未来日
       recentOrder.setTotalAmount(3000);
       recentOrder.setCustomerName("未来の注文");
       recentOrder.setCustomerEmail("future@example.com");
       recentOrder.setOrderStatus("PENDING");
       recentOrder.setPhoneNumber("090-0000-0001");
       recentOrder.setShippingAddress("東京都未来区1-1-1");

       Order oldOrder = new Order();
       oldOrder.setOrderDate(now.minusDays(1)); // 過去日
       oldOrder.setTotalAmount(1000);
       oldOrder.setCustomerName("過去の注文");
       oldOrder.setCustomerEmail("past@example.com");
       oldOrder.setOrderStatus("PENDING");
       oldOrder.setPhoneNumber("090-0000-0002");
       oldOrder.setShippingAddress("東京都過去区2-2-2");

       orderRepository.saveAll(List.of(recentOrder, oldOrder));
       entityManager.flush();

       // Act
       List<Order> result = orderRepository.findByOrderDateAfter(now);

       // Assert
       assertThat(result).hasSize(1);
       assertThat(result.get(0).getCustomerName()).isEqualTo("未来の注文");
    }

    @Test
    @DisplayName("注文が存在しない場合findAllは空のリストを返す")
    void findAll_WhenNoOrders_ShouldReturnEmptyList() {
        // Arrange (データなしの状態)

        // Act
        List<Order> orders = orderRepository.findAll();

        // Assert
        assertThat(orders).isEmpty(); // 空のリストが返ること
    }

    @Test
    @DisplayName("注文を更新できる")
    void updateOrder_ShouldReflectChanges() {
        // Arrange
        Order order = createSampleOrder("更新前顧客");
        Order savedOrder = entityManager.persistFlushFind(order);
        Integer orderId = savedOrder.getId();
        LocalDateTime initialUpdatedAt = savedOrder.getUpdatedAt(); // 初期の更新日時
        entityManager.detach(savedOrder); 

        // Act
        // 更新対象のOrderを取得
        Order orderToUpdate = orderRepository.findById(orderId).orElseThrow();
        String newStatus = "SHIPPED"; // 新しいステータス
        String newAddress = "更新後の住所"; // 新しい住所
        orderToUpdate.setOrderStatus(newStatus); // ステータスを変更
        orderToUpdate.setShippingAddress(newAddress); // 住所を変更
        orderRepository.save(orderToUpdate); // 更新処理 
        entityManager.flush();
        entityManager.clear();

        // Assert
        Order updatedOrder = entityManager.find(Order.class, orderId); // 更新後のデータをDBから取得
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(newStatus); // ステータスが更新されている
        assertThat(updatedOrder.getShippingAddress()).isEqualTo(newAddress); // 住所が更新されている
        assertThat(updatedOrder.getCustomerName()).isEqualTo(order.getCustomerName()); // 変更していない項目はそのまま
        assertThat(updatedOrder.getUpdatedAt()).isAfter(initialUpdatedAt); // @PreUpdateによりupdatedAtが更新されているはず
    }


    @Test
    @DisplayName("IDを指定して注文を削除できる (関連する詳細も削除される)")
    void deleteById_ShouldRemoveOrderAndDetails() {
        // Arrange
        Order order = createSampleOrder("削除対象顧客");
        Order savedOrder = entityManager.persistFlushFind(order);
        Integer orderId = savedOrder.getId();
        // 削除前のOrderDetailのIDを取得 (削除確認用)
        List<Integer> detailIds = savedOrder.getOrderDetails().stream()
                                           .map(OrderItem::getId)
                                           .toList();
        assertThat(detailIds).isNotEmpty(); // 詳細が存在することを前提とする
        entityManager.clear();

        // Act
        // 削除前に存在することを確認
        assertThat(orderRepository.findById(orderId)).isPresent();
        assertThat(orderItemRepository.findById(detailIds.get(0))).isPresent();

        orderRepository.deleteById(orderId); // IDで削除
        entityManager.flush(); // DBに反映
        entityManager.clear();

        // Assert
        // Orderが削除されたことを確認
        assertThat(orderRepository.findById(orderId)).isNotPresent();
        // 関連するOrderDetailも削除されていることを確認 (Orderエンティティの CascadeType.ALL と orphanRemoval = true による)
        for (Integer detailId : detailIds) {
             assertThat(orderItemRepository.findById(detailId)).isNotPresent();
             // entityManager.findでも確認可能
             // assertThat(entityManager.find(OrderDetail.class, detailId)).isNull();
        }
    }


    @Test
    @DisplayName("必須項目nullで保存しようとするとDataIntegrityViolationExceptionが発生する")
    void saveOrder_WithNullRequiredField_ShouldThrowException() {
        // Arrange
        Order order = createSampleOrder("制約違反顧客");
        order.setCustomerName(null); // @Column(nullable = false) のカラムにnullを設定

        // Act & Assert
        // save() の時点では例外は発生せず、flush() のタイミングでDB制約により発生することが多い
        assertThatThrownBy(() -> {
            orderRepository.save(order);
            entityManager.flush(); // DBへの反映時に制約違反が発生
        })
        .isInstanceOf(DataIntegrityViolationException.class) // Spring Data JPAがラップした例外
        .hasCauseInstanceOf(PersistenceException.class); // JPAレイヤーの例外が原因
        // .hasMessageContaining("NULL not allowed for column \"CUSTOMER_NAME\""); // DB依存のエラーメッセージ確認は脆い場合がある
    }

    @Test
    void 注文と注文商品が正常に保存される() {
        Product product = createProduct("マグカップ", 1200);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now()); 
        order.setCustomerName("テスト顧客");
        order.setCustomerEmail("test@example.com");
        order.setShippingAddress("東京都新宿区");
        order.setPhoneNumber("090-1234-5678"); 
        order.setOrderStatus("PENDING"); 
        order.setTotalAmount(2400); 

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(2);
        item.setSubtotal(2400);
        order.setOrderDetails(List.of(item));

        Order saved = orderRepository.save(order);
        entityManager.flush();

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getOrderDetails()).hasSize(1);
        assertThat(saved.getOrderDetails().get(0).getSubtotal()).isEqualTo(2400);
    }

    @Test
    void 商品がnullだと保存に失敗する() {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerName("テスト顧客");
        order.setCustomerEmail("test@example.com");
        order.setShippingAddress("東京都千代田区");
        order.setPhoneNumber("090-1234-5678");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(2400);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(null); // エラーになるはず
        item.setProductName("マグカップ");
        item.setUnitPrice(1200);
        item.setQuantity(2);
        item.setSubtotal(2400);
        order.setOrderDetails(List.of(item));

        order.setOrderDate(LocalDateTime.now());
        order.setCustomerName("検証用顧客");
        order.setCustomerEmail("test@example.com");
        order.setShippingAddress("東京都千代田区");
        order.setPhoneNumber("090-0000-0000");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(2400);

        assertThatThrownBy(() -> {
            orderRepository.save(order);
            entityManager.flush();
        }).hasRootCauseInstanceOf(org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException.class);
    }

    @Test
    @DisplayName("数量が0やマイナスだと不正（現状はDB制約なし、将来的にバリデーション検討）")
    void 数量が0やマイナスだと不正() {
        Product product = createProduct("タオル", 800);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerName("テスト顧客");
        order.setCustomerEmail("test@example.com"); 
        order.setShippingAddress("東京都千代田区");
        order.setPhoneNumber("090-1234-5678");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(2400);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(0); // 無効な数量（0）
        item.setSubtotal(0);
        order.setOrderDetails(List.of(item));

        Order saved = orderRepository.save(order);
        entityManager.flush();

        // DB制約では弾かれないがビジネスルールとしては数量は1以上が望ましい
        // 将来的にはサービス層またはバリデーションアノテーションで制御する予定
        assertThat(saved.getOrderDetails().get(0).getQuantity()).isEqualTo(0);
}

    @Test
    @DisplayName("小計が単価と数量に一致しない場合の挙動（現状はDB制約なし、将来的にサービス層での整合性チェック推奨）")
    void 小計が単価と数量に一致しないと不正() {
        Product product = createProduct("ノート", 500);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerName("テスト顧客");
        order.setCustomerEmail("test@example.com"); 
        order.setShippingAddress("東京都千代田区");
        order.setPhoneNumber("090-1234-5678");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(2400);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setUnitPrice(500);
        item.setQuantity(3);
        item.setSubtotal(1000); // 本来は 1500 であるべきところ不一致の値
        order.setOrderDetails(List.of(item));

        Order saved = orderRepository.save(order);
        entityManager.flush();

        // DB制約では弾かれないがビジネスルールとしては
        // 単価 * 数量 と小計は一致しているべき
        // 将来的にはサービス層での整合性チェックやバリデーション追加を検討する必要がある
        assertThat(saved.getOrderDetails().get(0).getSubtotal()).isNotEqualTo(1500);
    }

        @Test
        void 複数商品が正しく保存される() {
        Product p1 = createProduct("皿", 1000);
        Product p2 = createProduct("フォーク", 300);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerName("テスト顧客");
        order.setCustomerEmail("test@example.com"); 
        order.setShippingAddress("東京都千代田区");
        order.setPhoneNumber("090-1234-5678");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(2400);

        OrderItem i1 = new OrderItem();
        i1.setOrder(order);
        i1.setProduct(p1);
        i1.setProductName(p1.getName());
        i1.setUnitPrice(p1.getPrice());
        i1.setQuantity(1);
        i1.setSubtotal(1000);

        OrderItem i2 = new OrderItem();
        i2.setOrder(order);
        i2.setProduct(p2);
        i2.setProductName(p2.getName());
        i2.setUnitPrice(p2.getPrice());
        i2.setQuantity(2);
        i2.setSubtotal(600);

        order.setOrderDetails(List.of(i1, i2));
        Order saved = orderRepository.save(order);
        entityManager.flush();

        assertThat(saved.getOrderDetails()).hasSize(2);
    }

        @Test
        void createdAtが自動で設定される() {
        Product product = createProduct("時計", 3000);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerName("テスト顧客");
        order.setCustomerEmail("test@example.com"); 
        order.setShippingAddress("東京都千代田区");
        order.setPhoneNumber("090-1234-5678");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(2400);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(1);
        item.setSubtotal(3000);
        order.setOrderDetails(List.of(item));

        Order saved = orderRepository.save(order);
        entityManager.flush();

        assertThat(saved.getOrderDetails().get(0).getCreatedAt()).isNotNull();
    }

}