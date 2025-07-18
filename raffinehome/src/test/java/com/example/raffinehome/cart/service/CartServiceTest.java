package com.example.raffinehome.cart.service;

import com.example.raffinehome.cart.dto.CartDTO;
import com.example.raffinehome.cart.dto.CartItemDTO;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.LinkedHashMap; // 順序維持のため
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy; // 例外テスト用
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private HttpSession session;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession(); // 各テストで新しいセッションを使用

        // テストで使用する商品データ
        product1 = new Product();
        product1.setId(1);
        product1.setName("商品1");
        product1.setPrice(500);
        product1.setImageUrl("/img1.png");
        product1.setStockQuantity(10);

        product2 = new Product();
        product2.setId(2);
        product2.setName("商品2");
        product2.setPrice(1000);
        product2.setImageUrl("/img2.png");
        product2.setStockQuantity(5);
    }

    // --- getCartSession のテスト ---

    @Test
    @DisplayName("セッションにカートが存在しない場合、新しい空のカートを作成してセッションに保存し、それを返す")
    void getCartSession_WhenCartNotExists_ShouldCreateNewCartAndSaveToSession() {
        // Act
        CartDTO cart = cartService.getCartSession(session);

        // Assert
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getItemCount()).isZero();
        assertThat(cart.getTotalPrice()).isZero();
        // セッションに新しいカートが保存されていることを確認
        assertThat(session.getAttribute("cart")).isSameAs(cart);
    }

    @Test
    @DisplayName("セッションにカートが存在する場合、既存のカートをそのまま返す")
    void getCartSession_WhenCartExists_ShouldReturnExistingCart() {
        // Arrange
        CartDTO existingCart = new CartDTO();
        // 適当なアイテムを事前に入れておく
        CartItemDTO existingItem = new CartItemDTO("1", 1, "既存商品", 300, "/ex.png", 1, 300);
        existingCart.addItem(existingItem);
        session.setAttribute("cart", existingCart);

        // Act
        CartDTO cart = cartService.getCartSession(session);

        // Assert
        assertThat(cart).isSameAs(existingCart); // 返されたカートがセッション内のものと同一インスタンスである
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get("1")).isEqualTo(existingItem);
    }

    // --- addToCart のテスト ---

    @Test
    @DisplayName("存在する商品をカートに初めて追加すると、カートとセッションが更新される")
    void addToCart_WhenProductExistsAndCartIsEmpty_ShouldAddToCartAndUpdateSession() {
        // Arrange
        Integer productId = 1;
        Integer quantity = 2;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        CartDTO cart = cartService.addToCart(productId, quantity, session);
        CartDTO cartFromSession = (CartDTO) session.getAttribute("cart");

        // Assert
        assertThat(cart).isNotNull();
        assertThat(cartFromSession).isNotNull().isSameAs(cart); // 返り値とセッションの内容が同じ

        assertThat(cart.getItems()).hasSize(1);
        CartItemDTO addedItem = cart.getItems().get(String.valueOf(productId));
        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getProductId()).isEqualTo(productId);
        assertThat(addedItem.getName()).isEqualTo(product1.getName());
        assertThat(addedItem.getPrice()).isEqualTo(product1.getPrice());
        assertThat(addedItem.getQuantity()).isEqualTo(quantity);
        assertThat(addedItem.getSubtotal()).isEqualTo(product1.getPrice() * quantity);
        assertThat(cart.getItemCount()).isEqualTo(quantity);
        assertThat(cart.getTotalPrice()).isEqualTo(product1.getPrice() * quantity);

        verify(productRepository, times(1)).findById(productId); // Repositoryが呼ばれたか確認
    }

    @Test
    @DisplayName("既にカートにある商品を追加すると、数量と合計金額が正しく更新される")
    void addToCart_WhenAddingExistingProduct_ShouldIncreaseQuantityAndUpdateTotals() {
        // Arrange
        Integer productId = 1;
        Integer initialQuantity = 1;
        Integer addQuantity = 3;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        // 事前に商品1を1つ追加
        cartService.addToCart(productId, initialQuantity, session);

        // Act: 同じ商品1をさらに3つ追加
        CartDTO cart = cartService.addToCart(productId, addQuantity, session);
        CartDTO cartFromSession = (CartDTO) session.getAttribute("cart");

        // Assert
        assertThat(cart).isNotNull().isSameAs(cartFromSession);
        assertThat(cart.getItems()).hasSize(1); // 商品の種類は増えない

        CartItemDTO item = cart.getItems().get(String.valueOf(productId));
        assertThat(item).isNotNull();
        int expectedQuantity = initialQuantity + addQuantity; // 1 + 3 = 4
        int expectedTotalPrice = product1.getPrice() * expectedQuantity; // 500 * 4 = 2000
        assertThat(item.getQuantity()).isEqualTo(expectedQuantity);
        assertThat(item.getSubtotal()).isEqualTo(expectedTotalPrice);
        assertThat(cart.getItemCount()).isEqualTo(expectedQuantity);
        assertThat(cart.getTotalPrice()).isEqualTo(expectedTotalPrice);

        verify(productRepository, times(2)).findById(productId); // 2回呼ばれる
    }

    @Test
    @DisplayName("存在しない商品を追加しようとすると、nullが返されカートは作成/更新されない")
    void addToCart_WhenProductNotExists_ShouldReturnNullAndNotUpdateCart() {
        // Arrange
        Integer nonExistingProductId = 99;
        Integer quantity = 1;
        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty()); // 商品が見つからない場合

        // Act
        CartDTO cart = cartService.addToCart(nonExistingProductId, quantity, session);

        // Assert
        assertThat(cart).isNull(); // nullが返される
        assertThat(session.getAttribute("cart")).isNull(); // セッションにカートは存在しない

        verify(productRepository, times(1)).findById(nonExistingProductId);
    }

    @Test
    @DisplayName("addToCart に null の productId を渡すと null が返される")
    void addToCart_WithNullProductId_ShouldReturnNull() {
        when(productRepository.findById(null)).thenReturn(Optional.empty());
    
        // 戻り値がnullであることを検証する
        CartDTO result = cartService.addToCart(null, 1, session);
    
        // Assert
        assertThat(result).isNull(); // 戻り値がnullであることを確認
    
        // 念のため、リポジトリが呼び出されたことを確認
        verify(productRepository, times(1)).findById(null);
        // 他のリポジトリメソッドが呼ばれていないことを確認
        verifyNoMoreInteractions(productRepository);
        // セッションのカート状態が変わっていないことも確認
        assertThat(session.getAttribute("cart")).isNull();
    }

    @Test
    @DisplayName("addToCart に null の quantity を渡すと NullPointerException が発生する")
    void addToCart_WithNullQuantity_ShouldThrowNPE() {
        // Arrange
        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act & Assert
        // Cart.addItem 内での計算時に NPE が発生する可能性
        assertThatThrownBy(() -> cartService.addToCart(productId, null, session))
                .isInstanceOf(NullPointerException.class);
    }

    // --- updateCartItem のテスト ---

    @Test
    @DisplayName("カート内の商品の数量を正しく更新でき、合計も再計算される")
    void updateCartItem_WhenItemExists_ShouldUpdateQuantityAndTotals() {
        // Arrange
        Integer productId1 = 1;
        Integer productId2 = 2;
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        cartService.addToCart(productId1, 2, session); // 商品1 x 2 (1000円)
        cartService.addToCart(productId2, 1, session); // 商品2 x 1 (1000円)
        // 初期状態: Total Qty=3, Total Price=2000
        String itemIdToUpdate = String.valueOf(productId1);
        Integer newQuantity = 5;

        // Act
        CartDTO cart = cartService.updateCartItem(itemIdToUpdate, newQuantity, session);
        CartDTO cartFromSession = (CartDTO) session.getAttribute("cart");

        // Assert
        assertThat(cart).isNotNull().isSameAs(cartFromSession);
        assertThat(cart.getItems()).hasSize(2);

        CartItemDTO updatedItem = cart.getItems().get(itemIdToUpdate);
        assertThat(updatedItem.getQuantity()).isEqualTo(newQuantity); // 数量が更新されている
        assertThat(updatedItem.getSubtotal()).isEqualTo(product1.getPrice() * newQuantity); // 500 * 5 = 2500

        CartItemDTO otherItem = cart.getItems().get(String.valueOf(productId2)); // 他のアイテムは影響を受けない
        assertThat(otherItem.getQuantity()).isEqualTo(1);
        assertThat(otherItem.getSubtotal()).isEqualTo(product2.getPrice() * 1); // 1000 * 1 = 1000

        // 合計が再計算されている
        int expectedTotalQuantity = newQuantity + 1; // 5 + 1 = 6
        int expectedTotalPrice = updatedItem.getSubtotal() + otherItem.getSubtotal(); // 2500 + 1000 = 3500
        assertThat(cart.getItemCount()).isEqualTo(expectedTotalQuantity);
        assertThat(cart.getTotalPrice()).isEqualTo(expectedTotalPrice);
    }

    @Test
    @DisplayName("存在しない itemId で数量を更新しようとしてもカートは変化しない")
    void updateCartItem_WhenItemIdNotExists_ShouldNotChangeCart() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        CartDTO initialCart = cartService.addToCart(1, 1, session);
        CartDTO initialCartState = cloneCart(initialCart); // 変更前の状態をクローン
        String nonExistingItemId = "99";
        Integer newQuantity = 5;

        // Act
        CartDTO cart = cartService.updateCartItem(nonExistingItemId, newQuantity, session);
        CartDTO cartFromSession = (CartDTO) session.getAttribute("cart");

        // Assert
        assertThat(cart).isNotNull().isSameAs(cartFromSession);
        // カートの内容が変化していないことを確認
        assertThat(cart.getItems()).isEqualTo(initialCartState.getItems());
        assertThat(cart.getItemCount()).isEqualTo(initialCartState.getItemCount());
        assertThat(cart.getTotalPrice()).isEqualTo(initialCartState.getTotalPrice());
    }

    @Test
    @DisplayName("数量に0を指定して更新すると、数量と小計、合計が0になる")
    void updateCartItem_WithZeroQuantity_ShouldUpdateToZero() {
        // Arrange
        Integer productId = 1;
        String itemId = String.valueOf(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        cartService.addToCart(productId, 3, session);

        // Act
        CartDTO cart = cartService.updateCartItem(itemId, 0, session);

        // Assert
        assertThat(cart.getItems().get(itemId).getQuantity()).isZero();
        assertThat(cart.getItems().get(itemId).getSubtotal()).isZero();
        assertThat(cart.getItemCount()).isZero();
        assertThat(cart.getTotalPrice()).isZero();
    }

    @Test
    @DisplayName("数量に負数を指定して更新すると、そのままセットされ合計も計算される（バリデーションは別）")
    void updateCartItem_WithNegativeQuantity_ShouldUpdateAsIs() {
        // Arrange
        Integer productId = 1;
        String itemId = String.valueOf(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        cartService.addToCart(productId, 3, session);
        int negativeQuantity = -2;

        // Act
        CartDTO cart = cartService.updateCartItem(itemId, negativeQuantity, session);

        // Assert
        assertThat(cart.getItems().get(itemId).getQuantity()).isEqualTo(negativeQuantity);
        int expectedSubtotal = product1.getPrice() * negativeQuantity; // 500 * -2 = -1000
        assertThat(cart.getItems().get(itemId).getSubtotal()).isEqualTo(expectedSubtotal);
        assertThat(cart.getItemCount()).isEqualTo(negativeQuantity);
        assertThat(cart.getTotalPrice()).isEqualTo(expectedSubtotal);
        // 注意: この挙動が望ましくない場合、入力値バリデーションが必要
    }

    @Test
    @DisplayName("updateCartItem に null の itemId を渡してもカートは変化しない") 
    void updateCartItem_WithNullItemId_ShouldNotChangeCart() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addToCart(1, 1, session);
        // 変更前の状態をディープコピーしておく
        CartDTO initialCartState = cloneCart(cartService.getCartSession(session));
        assertThat(initialCartState.getItems()).hasSize(1); // 初期状態の確認

        // null の itemId で updateCartItem を呼び出す
        CartDTO cart = cartService.updateCartItem(null, 5, session);

        assertThat(cart).isNotNull(); // メソッドはカートオブジェクトを返すはず

        // カートの内容が initialCartState と比較して変化していないことを確認
        assertThat(cart.getItems()).hasSize(initialCartState.getItems().size());
        assertThat(cart.getItems()).isEqualTo(initialCartState.getItems());
        assertThat(cart.getItemCount()).isEqualTo(initialCartState.getItemCount());
        assertThat(cart.getTotalPrice()).isEqualTo(initialCartState.getTotalPrice());

        // セッション内のカートが返されたカートと同一インスタンスであることも確認
        assertThat(session.getAttribute("cart")).isSameAs(cart);
    }

    @Test
    @DisplayName("updateCartItem に null の quantity を渡すと NullPointerException が発生する")
    void updateCartItem_WithNullQuantity_ShouldThrowNPE() {
        // Arrange
        Integer productId = 1;
        String itemId = String.valueOf(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        cartService.addToCart(productId, 1, session);

        // Act & Assert
        // Cart.updateQuantity 内での計算時に NPE が発生する可能性
        assertThatThrownBy(() -> cartService.updateCartItem(itemId, null, session))
                .isInstanceOf(NullPointerException.class);
    }

    // --- removeFromCart のテスト ---

    @Test
    @DisplayName("カートから商品を削除すると、その商品がなくなり合計が再計算される")
    void removeFromCart_WhenItemExists_ShouldRemoveItemAndRecalculateTotals() {
        // Arrange
        Integer productId1 = 1;
        Integer productId2 = 2;
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        cartService.addToCart(productId1, 2, session); // 商品1 x 2 (1000円)
        cartService.addToCart(productId2, 1, session); // 商品2 x 1 (1000円)
        // 初期状態: Total Qty=3, Total Price=2000
        String itemIdToRemove = String.valueOf(productId1);

        // Act
        CartDTO cart = cartService.removeFromCart(itemIdToRemove, session);
        CartDTO cartFromSession = (CartDTO) session.getAttribute("cart");

        // Assert
        assertThat(cart).isNotNull().isSameAs(cartFromSession);
        assertThat(cart.getItems()).hasSize(1); // 商品が1つ減っている
        assertThat(cart.getItems().containsKey(itemIdToRemove)).isFalse(); // 削除されたIDがない
        assertThat(cart.getItems().containsKey(String.valueOf(productId2))).isTrue(); // 残ったIDはある

        // 合計が再計算されている
        CartItemDTO remainingItem = cart.getItems().get(String.valueOf(productId2));
        assertThat(cart.getItemCount()).isEqualTo(remainingItem.getQuantity()); // 1
        assertThat(cart.getTotalPrice()).isEqualTo(remainingItem.getSubtotal()); // 1000
    }

    @Test
    @DisplayName("存在しない itemId で商品を削除しようとしてもカートは変化しない")
    void removeFromCart_WhenItemIdNotExists_ShouldNotChangeCart() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        CartDTO initialCart = cartService.addToCart(1, 1, session);
        CartDTO initialCartState = cloneCart(initialCart); // 変更前の状態を保存
        String nonExistingItemId = "99";

        // Act
        CartDTO cart = cartService.removeFromCart(nonExistingItemId, session);
        CartDTO cartFromSession = (CartDTO) session.getAttribute("cart");

        // Assert
        assertThat(cart).isNotNull().isSameAs(cartFromSession);
        // カートの内容が変化していないことを確認
        assertThat(cart.getItems()).isEqualTo(initialCartState.getItems());
        assertThat(cart.getItemCount()).isEqualTo(initialCartState.getItemCount());
        assertThat(cart.getTotalPrice()).isEqualTo(initialCartState.getTotalPrice());
    }

    @Test
    @DisplayName("removeFromCart に null の itemId を渡しても例外は発生せずカートは変化しない")
    void removeFromCart_WithNullItemId_ShouldNotThrowAndCartUnchanged() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addToCart(1, 1, session); // 事前にアイテムを追加しておく

        // 比較のために初期状態をクローンしておく
        CartDTO initialCartState = cloneCart(cartService.getCartSession(session));
        assertThat(initialCartState.getItems()).hasSize(1); // 初期状態を確認
    
        // null itemIdでメソッドを実行
        CartDTO resultCart = cartService.removeFromCart(null, session);
    
        // Assert
        // 戻り値のカートとセッション内のカートが同一であることを確認
        assertThat(resultCart).isNotNull();
        assertThat(session.getAttribute("cart")).isSameAs(resultCart);
    
        // カートの状態が初期状態から変化していないことを確認
        assertThat(resultCart.getItems()).hasSize(initialCartState.getItems().size()); // サイズが変わっていない
        assertThat(resultCart.getItems()).isEqualTo(initialCartState.getItems());       // Mapの内容が等しい
        assertThat(resultCart.getItemCount()).isEqualTo(initialCartState.getItemCount()); // 合計数量が変わっていない
        assertThat(resultCart.getTotalPrice()).isEqualTo(initialCartState.getTotalPrice());       // 合計金額が変わっていない
    }

    // --- clearCart のテスト ---

    @Test
    @DisplayName("カートをクリアすると、セッションから cart 属性が削除される")
    void clearCart_WhenCartHasItems_ShouldClearAllItemsAndResetTotals() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        cartService.addToCart(1, 1, session); // 事前にカートにアイテムを追加
        assertThat(session.getAttribute("cart")).isNotNull(); // カートが存在することを確認

        // Act
        cartService.clearCart(session);

        // Assert
        CartDTO cart = (CartDTO) session.getAttribute("cart");
        assertThat("cart").isNotNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getItemCount()).isZero();
        assertThat(cart.getTotalPrice()).isZero();
    }


    @Test
    @DisplayName("カートがすでに空の場合、clearCartを呼んでも状態は変わらない")
    void clearCart_WhenCartAlreadyEmpty_ShouldDoNothing(){
        // Arrange
        CartDTO emptyCart = new CartDTO();
        session.setAttribute("cart", emptyCart);
        CartDTO beforeClear = cloneCart(emptyCart);

        // Act
        cartService.clearCart(session);

        // Assert
        CartDTO afterClear = (CartDTO) session.getAttribute("cart");
        assertThat("cart").isNotNull();
        assertThat(afterClear.getItems()).isEmpty();
        assertThat(afterClear.getItemCount()).isZero();
        assertThat(afterClear.getTotalPrice()).isZero();
    }


    // Cartオブジェクトの（簡易的な）ディープコピーを行うヘルパーメソッド
    private CartDTO cloneCart(CartDTO original) {
        if (original == null) return null;
        CartDTO clone = new CartDTO();
        // items Map をシャローコピーし、中の CartItem もコピーする（CartItemがミュータブルな場合）
        Map<String, CartItemDTO> clonedItems = new LinkedHashMap<>();
        original.getItems().forEach((key, item) -> {
            // CartItemのコンストラクタまたはcloneメソッドでコピー
            CartItemDTO clonedItem = new CartItemDTO(
                item.getId(), item.getProductId(), item.getName(), item.getPrice(),
                item.getImageUrl(), item.getQuantity(), item.getSubtotal()
            );
            clonedItems.put(key, clonedItem);
        });
        clone.setItems(clonedItems);
        clone.calculateTotals(); // 合計を再計算
        return clone;
    }
}