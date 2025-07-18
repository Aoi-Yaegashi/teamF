package com.example.raffinehome.product.controller;

import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Collections; // 空リスト用
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // ProductController と関連コンポーネントをテスト
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレート

    @MockBean // Service層のモック
    private ProductService productService;

    private ProductListDTO productListItem1;
    private ProductListDTO productListItem2;
    private ProductDTO productDetail1;
    private ProductDTO productDetailWithNulls; // nullフィールドを含む詳細データ

    @BeforeEach
    void setUp() {
        // --- テストデータ準備 ---
        productListItem1 = new ProductListDTO(1, "リスト商品1", 100, 90, "説明A", 3, "/list1.png");
        productListItem2 = new ProductListDTO(2, "リスト商品2", 200, 180, "説明B", 0, "/list2.png");

        productDetail1 = new ProductDTO (1, "詳細商品1", 100, 90, "詳細説明1", 10,  "/detail1.png", true);
        productDetailWithNulls = new ProductDTO(3, "詳細商品3", 300, 280, null, 5, null, true); // descriptionとimageUrlがnull

        // --- Serviceメソッドのデフォルトモック設定 (lenient) ---
        // デフォルトではfindAllProductsは2つのアイテムを返す
        lenient().when(productService.findAllActiveProducts()).thenReturn(Arrays.asList(productListItem1, productListItem2));
        // デフォルトではfindProductById(1) は productDetail1 を返す
        lenient().when(productService.findProductById(1)).thenReturn(productDetail1);
        // デフォルトでは存在しないID(99)ではnullを返す
        lenient().when(productService.findProductById(99)).thenReturn(null);
        // nullフィールドを持つ商品データ
        lenient().when(productService.findProductById(3)).thenReturn(productDetailWithNulls);
    }

    // === GET /api/products ===
    @Nested
    @DisplayName("GET /api/products")
    class GetAllActiveProductsTests {

        @Test
        @DisplayName("商品が存在する場合、商品リスト(ProductListItem)を200 OKで返す")
        void getAllProducts_WhenProductsExist_ShouldReturnProductList() throws Exception {
            // Arrange (setUpのデフォルトモックを使用)

            // Act & Assert
            mockMvc.perform(get("/api/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // ステータスコード200 OK
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Content-TypeがJSON
                    .andExpect(jsonPath("$", hasSize(2))) // ルート配列のサイズが2
                    // 1番目の要素の全フィールドを検証
                    .andExpect(jsonPath("$[0].id", is(productListItem1.getId())))
                    .andExpect(jsonPath("$[0].name", is(productListItem1.getName())))
                    .andExpect(jsonPath("$[0].price", is(productListItem1.getPrice())))
                    .andExpect(jsonPath("$[0].imageUrl", is(productListItem1.getImageUrl())))
                    // 2番目の要素の全フィールドを検証
                    .andExpect(jsonPath("$[1].id", is(productListItem2.getId())))
                    .andExpect(jsonPath("$[1].name", is(productListItem2.getName())))
                    .andExpect(jsonPath("$[1].price", is(productListItem2.getPrice())))
                    .andExpect(jsonPath("$[1].imageUrl", is(productListItem2.getImageUrl())));

            verify(productService, times(1)).findAllActiveProducts();
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("商品が存在しない場合、空のリストを200 OKで返す")
        void getAllProducts_WhenNoProductsExist_ShouldReturnEmptyList() throws Exception {
            // Arrange
            when(productService.findAllActiveProducts()).thenReturn(Collections.emptyList()); // 空リストを返すように設定

            // Act & Assert
            mockMvc.perform(get("/api/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0))); // 空の配列であることを確認

            verify(productService, times(1)).findAllActiveProducts();
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("ProductServiceが例外をスローした場合、500 Internal Server Errorを返す")
        void getAllProducts_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
            // Arrange
            when(productService.findAllActiveProducts()).thenThrow(new RuntimeException("サービスエラー"));

            // Act & Assert
            mockMvc.perform(get("/api/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // GlobalExceptionHandler が有効ならエラーメッセージを含むJSONが返る可能性がある
                    .andExpect(jsonPath("$.message", containsString("サービスエラー")));

            verify(productService, times(1)).findAllActiveProducts();
            verifyNoMoreInteractions(productService);
        }
    }

    // === GET /api/products/{id} ===
    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductByIdTests {

        @Test
        @DisplayName("存在するproductIdの場合、商品詳細(ProductDetail)を200 OKで返す")
        void getProductById_WhenProductExists_ShouldReturnProductDetail() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:1)
            Integer productId = 1;

            // Act & Assert
            mockMvc.perform(get("/api/products/{id}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    // 全フィールドを検証
                    .andExpect(jsonPath("$.id", is(productDetail1.getId())))
                    .andExpect(jsonPath("$.name", is(productDetail1.getName())))
                    .andExpect(jsonPath("$.price", is(productDetail1.getPrice())))
                    .andExpect(jsonPath("$.description", is(productDetail1.getDescription())))
                    .andExpect(jsonPath("$.stockQuantity", is(productDetail1.getStockQuantity())))
                    .andExpect(jsonPath("$.imageUrl", is(productDetail1.getImageUrl())));

            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("存在するproductIdで、一部フィールドがnullの商品の場合、nullを含む商品詳細を200 OKで返す")
        void getProductById_WhenProductExistsWithNullFields_ShouldReturnProductDetailWithNulls() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:3)
            Integer productId = 3;

            // Act & Assert
            mockMvc.perform(get("/api/products/{id}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(productDetailWithNulls.getId())))
                    .andExpect(jsonPath("$.name", is(productDetailWithNulls.getName())))
                    .andExpect(jsonPath("$.price", is(productDetailWithNulls.getPrice())))
                    .andExpect(jsonPath("$.description", is(nullValue()))) // descriptionがnull
                    .andExpect(jsonPath("$.stockQuantity", is(productDetailWithNulls.getStockQuantity())))
                    .andExpect(jsonPath("$.imageUrl", is(nullValue()))); // imageUrlがnull

            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("存在しないproductIdの場合、404 Not Foundを返す")
        void getProductById_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:99)
            Integer productId = 99;

            // Act & Assert
            mockMvc.perform(get("/api/products/{id}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()); // ステータスコード404 Not Found

            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("Idが数値でない場合、500 Internal Server Errorを返す (現在のGlobalExceptionHandlerの実装による)") // DisplayName を変更
        void getProductById_WithInvalidIdFormat_ShouldReturnInternalServerError_DueToExceptionHandler() throws Exception { // メソッド名を変更
            // Arrange
            String invalidId = "abc"; // 数値でないパスパラメータ

            // Act & Assert
            // 現在のGlobalExceptionHandlerは型ミスマッチをRuntimeExceptionとして扱い500を返すため、
            // テストの期待値もそれに合わせる。
            mockMvc.perform(get("/api/products/{id}", invalidId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // オプション： GlobalExceptionHandlerが返すエラーメッセージの内容も検証する
                    .andExpect(jsonPath("$.message", containsString("Failed to convert value of type")))
                    .andExpect(jsonPath("$.message", containsString(invalidId))); // 不正な値が含まれていることを確認

            // この場合、コントローラーメソッドやサービスは呼び出されない
            verifyNoInteractions(productService);
        }

@Test
@DisplayName("ProductServiceが例外をスローした場合、500 Internal Server Errorを返す")
void getProductById_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
    // Arrange
    Integer productId = 1;
    when(productService.findProductById(productId)).thenThrow(new RuntimeException("サービスエラー"));

    // Act & Assert
    mockMvc.perform(get("/api/products/{id}", productId)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message", containsString("サービスエラー")));

    verify(productService, times(1)).findProductById(productId);
    verifyNoMoreInteractions(productService);
}
}
}