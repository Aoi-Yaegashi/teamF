package com.example.raffinehome.cart.controller;

import com.example.raffinehome.cart.dto.CartAddDTO;
import com.example.raffinehome.cart.dto.CartDTO;
import com.example.raffinehome.cart.dto.CartItemDTO;
import com.example.raffinehome.cart.dto.CartUpdateDTO;
import com.example.raffinehome.cart.service.CartService;
import com.example.raffinehome.common.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions; // ResultActions用

import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class) // CartController とその依存関係（バリデーションなど）をテスト
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレート

    @Autowired
    private ObjectMapper objectMapper; // JSON <-> Object 変換

    @MockBean // Service層のモック
    private CartService cartService;
    private CartDTO cartWithOneItem;
    private CartDTO emptyCart;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new MockHttpSession(); // 各テストで新しいセッション

        // --- テストデータ準備 ---
        cartWithOneItem = new CartDTO();
        CartItemDTO item1 = new CartItemDTO("1", 1, "カート商品1", 1000, "/c1.png", 2, 2000);
        cartWithOneItem.setItems(Map.of("1", item1));
        cartWithOneItem.calculateTotals(); // totalQuantity=2, totalPrice=2000

        emptyCart = new CartDTO(); // items={}, totalQuantity=0, totalPrice=0
        emptyCart.calculateTotals();

        // --- Serviceメソッドのデフォルトモック設定 (lenient) ---
        // getCart はデフォルトで空のカートを返す
        lenient().when(cartService.getCart(any(HttpSession.class))).thenReturn(emptyCart);
        // 他のメソッドはテストごとに when で設定
    }

    // === GET /api/cart ===
    @Nested
    @DisplayName("GET /api/cart")
    class GetCartTests {
        @Test
        @DisplayName("セッションにカートが存在する場合、カート情報を200 OKで返す")
        void getCart_WhenCartExists_ShouldReturnCartWithStatusOk() throws Exception {
            // Arrange
            when(cartService.getCart(any(HttpSession.class))).thenReturn(cartWithOneItem);

            // Act & Assert
            mockMvc.perform(get("/api/cart")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON)) // Acceptヘッダーを追加
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.itemCount", is(cartWithOneItem.getItemCount())))
                    .andExpect(jsonPath("$.totalPrice", is(cartWithOneItem.getTotalPrice())))
                    .andExpect(jsonPath("$.items", hasKey("1"))) // item "1" が存在するか
                    .andExpect(jsonPath("$.items.1.productId", is(1)))
                    .andExpect(jsonPath("$.items.1.name", is("カート商品1")))
                    .andExpect(jsonPath("$.items.1.quantity", is(2)))
                    .andExpect(jsonPath("$.items.1.subtotal", is(2000)));

            verify(cartService, times(1)).getCart(any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }

        @Test
        @DisplayName("セッションにカートが存在しない場合、空のカート情報を200 OKで返す")
        void getCart_WhenCartNotExists_ShouldReturnEmptyCartWithStatusOk() throws Exception {
            // Arrange (setUpのデフォルトモックを使用)

            // Act & Assert
            mockMvc.perform(get("/api/cart")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.itemCount", is(0)))
                    .andExpect(jsonPath("$.totalPrice", is(0)))
                    .andExpect(jsonPath("$.items", anEmptyMap())); // itemsが空のMapか

            verify(cartService, times(1)).getCart(any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }

        @Test
        @DisplayName("CartServiceが例外をスローした場合、500 Internal Server Errorを返す")
        void getCart_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
            // Arrange
            when(cartService.getCart(any(HttpSession.class))).thenThrow(new RuntimeException("サービスエラー"));

            // Act & Assert
            mockMvc.perform(get("/api/cart")
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // GlobalExceptionHandler が有効ならエラーメッセージを含むJSONが返る可能性がある
                    .andExpect(jsonPath("$.message", containsString("サービスエラー")));


            verify(cartService, times(1)).getCart(any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }
    }

    // === POST /api/cart ===
    @Nested
    @DisplayName("POST /api/cart/add")
    class AddItemTests {
        @Test
        @DisplayName("有効な商品情報の場合、カートに追加し更新されたカートを200 OKで返す")
        void addItem_WithValidData_ShouldReturnUpdatedCartWithStatusOk() throws Exception {
            // Arrange
            CartAddDTO dto = new CartAddDTO();
            dto.setProductId(1);
            dto.setQuantity(2);

            when(cartService.addToCart(eq(dto.getProductId()), eq(dto.getQuantity()), any(HttpSession.class)))
                    .thenReturn(cartWithOneItem); // 更新後のカートを返すように設定

            // Act & Assert
            mockMvc.perform(post("/api/cart/add")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.itemCount", is(cartWithOneItem.getItemCount())))
                    .andExpect(jsonPath("$.totalPrice", is(cartWithOneItem.getTotalPrice())))
                    .andExpect(jsonPath("$.items.1.quantity", is(2)));

            verify(cartService, times(1)).addToCart(eq(dto.getProductId()), eq(dto.getQuantity()), any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }

        @Test
        @DisplayName("CartServiceがnullを返す場合（商品が見つからない等）、404 Not Foundを返す")
        void addItem_WhenServiceReturnsNull_ShouldReturnNotFound() throws Exception {
            // Arrange
            CartAddDTO dto = new CartAddDTO();
            dto.setProductId(99); // 存在しない商品ID
            dto.setQuantity(1);

            when(cartService.addToCart(eq(dto.getProductId()), eq(dto.getQuantity()), any(HttpSession.class)))
                    .thenReturn(null); // サービスがnullを返すケース

            // Act & Assert
            mockMvc.perform(post("/api/cart/add")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()); // 404 Not Found

            verify(cartService, times(1)).addToCart(eq(dto.getProductId()), eq(dto.getQuantity()), any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }

        // --- バリデーションテスト ---
        @Test
        @DisplayName("productIdがnullの場合、400 Bad Requestとエラーメッセージを返す")
        void addItem_WithNullProductId_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CartAddDTO dto = new CartAddDTO();
            dto.setProductId(null); // NotNull違反
            dto.setQuantity(1);

            // Act & Assert
            ResultActions result = mockMvc.perform(post("/api/cart/add")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()); // 400 Bad Request

            // GlobalExceptionHandlerによるエラーレスポンスの検証
            result.andExpect(jsonPath("$.productId", is("商品IDは必須です")));

            verifyNoInteractions(cartService); // バリデーションエラーなのでサービスは呼ばれない
        }

        @Test
        @DisplayName("quantityがnullの場合、400 Bad Requestとエラーメッセージを返す")
        void addItem_WithNullQuantity_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CartAddDTO dto = new CartAddDTO();
            dto.setProductId(1);
            dto.setQuantity(null); // NotNull違反

            // Act & Assert
            mockMvc.perform(post("/api/cart/add")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantity", is("数量は必須です")));

            verifyNoInteractions(cartService);
        }

        @Test
        @DisplayName("quantityが0の場合、400 Bad Requestとエラーメッセージを返す")
        void addItem_WithZeroQuantity_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CartAddDTO dto = new CartAddDTO();
            dto.setProductId(1);
            dto.setQuantity(0); // Min(1)違反

            // Act & Assert
            mockMvc.perform(post("/api/cart/add")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantity", is("数量は1以上である必要があります")));

            verifyNoInteractions(cartService);
        }

         @Test
        @DisplayName("quantityが負数の場合、400 Bad Requestとエラーメッセージを返す")
        void addItem_WithNegativeQuantity_ShouldReturnBadRequest() throws Exception {
            // Arrange
            CartAddDTO dto = new CartAddDTO();
            dto.setProductId(1);
            dto.setQuantity(-1); // Min(1)違反

            // Act & Assert
            mockMvc.perform(post("/api/cart/add")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantity", is("数量は1以上である必要があります")));

            verifyNoInteractions(cartService);
        }
    }

    // === PUT /api/cart/update ===
    @Nested
    @DisplayName("PUT /api/cart/update")
    class UpdateItemTests {
        @Test
        @DisplayName("有効な数量の場合、カートを更新し更新されたカートを200 OKで返す")
        void updateItem_WithValidData_ShouldReturnUpdatedCartWithStatusOk() throws Exception {
            // Arrange
            String productId = "1";
            CartUpdateDTO dto = new CartUpdateDTO();
            dto.setProductId(1);
            dto.setQuantity(5);

            CartDTO updatedCart = new CartDTO(); // 更新後のカート (ダミー)
            CartItemDTO updatedItem = new CartItemDTO("1", 1, "カート商品1", 1000, "/c1.png", 5, 5000);
            updatedCart.setItems(Map.of("1", updatedItem)); //ここで商品を追加
            updatedCart.calculateTotals(); // itemCount=5, totalPrice=5000

            when(cartService.updateCartItem(eq(productId), eq(dto.getQuantity()), any(HttpSession.class)))
                    .thenReturn(updatedCart);

            // Act & Assert
            mockMvc.perform(put("/api/cart/update")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.itemCount", is(updatedCart.getItemCount())))
                    .andExpect(jsonPath("$.totalPrice", is(updatedCart.getTotalPrice())))
                    .andExpect(jsonPath("$.items.1.quantity", is(5))); // 更新された数量を確認

            verify(cartService, times(1)).updateCartItem(eq(productId), eq(dto.getQuantity()), any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }

         @Test
        @DisplayName("存在しないproductIdでもエラーにせずカートをそのまま返す") 
        void updateItem_WithNonExistingId_ShouldReturnOkAndUnchangedCart() throws Exception {
            // Arrange
            CartUpdateDTO dto = new CartUpdateDTO();
            dto.setProductId(9999);
            dto.setQuantity(3);

            // 既存の状態を想定
            CartDTO cart = new CartDTO();
            CartItemDTO existing = new CartItemDTO("1", 1, "カート商品1", 1000, "/c1.png", 2, 2000);
            cart.setItems(Map.of("1", existing));
            cart.calculateTotals(); // itemCount=2 totalPrice=2000

            when(cartService.updateCartItem(eq("9999"), eq(3), any(HttpSession.class)))
                    .thenReturn(cart); // 変更なしで返す

            // Act & Assert
            mockMvc.perform(put("/api/cart/update")
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.itemCount", is(cart.getItemCount())))
                    .andExpect(jsonPath("$.items", hasKey("1")))
                    .andExpect(jsonPath("$.items.1.quantity", is(2))); // 変化なし
        }

        // --- バリデーションテスト ---
        @Test
        @DisplayName("quantityがnullの場合、400 Bad Requestとエラーメッセージを返す")
        void updateItem_WithNullQuantity_ShouldReturnBadRequest() throws Exception {
            // Arrange
            String productId = "1";
            CartUpdateDTO dto = new CartUpdateDTO();
            dto.setQuantity(null); // NotNull違反

            // Act & Assert
            mockMvc.perform(put("/api/cart/update", productId)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)) // 不正なボディ
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantity", is("数量は必須です")));

            verifyNoInteractions(cartService);
        }

        @Test
        @DisplayName("quantityが0の場合、400 Bad Requestとエラーメッセージを返す")
        void updateItem_WithZeroQuantity_ShouldReturnBadRequest() throws Exception {
            // Arrange
            String productId = "1";
            CartUpdateDTO dto = new CartUpdateDTO();
            dto.setQuantity(0); // Min(1)違反

            // Act & Assert
            mockMvc.perform(put("/api/cart/update", productId)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantity", is("数量は1以上である必要があります")));

            verifyNoInteractions(cartService);
        }

        @Test
        @DisplayName("quantityが負数の場合、400 Bad Requestとエラーメッセージを返す")
        void updateItem_WithNegativeQuantity_ShouldReturnBadRequest() throws Exception {
            // Arrange
            String productId = "1";
            CartUpdateDTO dto = new CartUpdateDTO();
            dto.setQuantity(-5); // Min(1)違反

            // Act & Assert
            mockMvc.perform(put("/api/cart/update", productId)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantity", is("数量は1以上である必要があります")));

            verifyNoInteractions(cartService);
        }
    }

    // === DELETE /api/cart/remove/{productId} ===
    @Nested
    @DisplayName("DELETE /api/cart/remove/{productId}")
    class RemoveItemTests {
        @Test
        @DisplayName("存在するproductIdの場合、カートから商品を削除し更新されたカートを200 OKで返す")
        void removeItem_WhenItemExists_ShouldReturnUpdatedCartWithStatusOk() throws Exception {
            // Arrange
            String productId = "1";
            // 削除後のカート（空）
            when(cartService.removeFromCart(eq(productId), any(HttpSession.class)))
                    .thenReturn(emptyCart);

            // Act & Assert
            mockMvc.perform(delete("/api/cart/remove/{productId}", productId)
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.itemCount", is(0)))
                    .andExpect(jsonPath("$.totalPrice", is(0)))
                    .andExpect(jsonPath("$.items", anEmptyMap())); // itemsが空であることを確認

            verify(cartService, times(1)).removeFromCart(eq(productId), any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }

         @Test
        @DisplayName("存在しないproductIdの場合でも、サービスがカートを返し、それを200 OKで返す")
        void removeItem_WhenItemNotExists_ShouldReturnCartFromServiceWithStatusOk() throws Exception {
            // Arrange
            String nonExistingItemId = "99";
            // 存在しないIDで削除しても、サービスは現在の（変化しない）カートを返す想定
            when(cartService.removeFromCart(eq(nonExistingItemId), any(HttpSession.class)))
                    .thenReturn(cartWithOneItem); // 例えば、削除前と同じカートが返る

            // Act & Assert
            mockMvc.perform(delete("/api/cart/remove/{productId}", nonExistingItemId)
                            .session(mockSession)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // 正常終了
                    .andExpect(jsonPath("$.itemCount", is(cartWithOneItem.getItemCount()))); // カート内容は変わらないはず

            verify(cartService, times(1)).removeFromCart(eq(nonExistingItemId), any(HttpSession.class));
            verifyNoMoreInteractions(cartService);
        }
    }

    // === DELETE /api/cart/clear ===
    @Nested
    @DisplayName("DELETE /api/cart/clear")
    class ClearCartTests {

        @Test
        @DisplayName("カートをクリアすると200 OK（ボディなし）")
        void clearCart_ShouldReturnOk() throws Exception {
            // `CartController` の実装は session.removeAttribute("cart") のみで service 不使用

            mockMvc.perform(delete("/api/cart/clear")
                            .session(mockSession))
                    .andExpect(status().isOk())
                    .andExpect(content().string(emptyString()));

            // サービスが呼ばれないことを確認
            verifyNoInteractions(cartService);
        }
    }
}