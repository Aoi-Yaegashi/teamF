package com.example.raffinehome.admin.controller;

import com.example.raffinehome.admin.service.AdminService;
import com.example.raffinehome.admin.dto.AdminProductDto;
import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections; // 空リスト用
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class) // ProductController と関連コンポーネントをテスト
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレート

    @MockBean // Service層のモック
    private AdminService adminService;

    @MockBean ProductService productService;

    private ProductListDTO productListItem1;
    private ProductListDTO productListItem2;
    private AdminProductDto productDetailForAdmin1;
    private AdminProductDto productDetailForAdminWithNulls; // nullフィールドを含む詳細データ

    @BeforeEach
    void setUp() {
        // --- テストデータ準備 ---
        productListItem1 = new ProductListDTO(1, "リスト商品1", 100, 90, "説明A", 3, "/list1.png");
        productListItem2 = new ProductListDTO(2, "リスト商品2", 200, 180, "説明B", 0, "/list2.png");

        productDetailForAdmin1 = new AdminProductDto (1, "詳細商品1","s詳細説明1" ,100, 90, 10,false,"/detail1.png",LocalDateTime.now(),LocalDateTime.now());
        productDetailForAdminWithNulls = new AdminProductDto(3, "詳細商品3",null,400, 300, 10, true,null,LocalDateTime.now(),LocalDateTime.now()); // descriptionとimageUrlがnull

        // --- Serviceメソッドのデフォルトモック設定 (lenient) ---
        // デフォルトではfindAllProductsは2つのアイテムを返す
        lenient().when(adminService.findAllProducts()).thenReturn(Arrays.asList(productListItem1, productListItem2));
        // デフォルトではfindProductById(1) は productDetail1 を返す
        lenient().when(adminService.findProductForAdminById(1)).thenReturn(productDetailForAdmin1);
        // デフォルトでは存在しないID(99)ではnullを返す
        lenient().when(adminService.findProductForAdminById(99)).thenReturn(null);
        // nullフィールドを持つ商品データ
        lenient().when(adminService.findProductForAdminById(3)).thenReturn(productDetailForAdminWithNulls);
    }
    
// === GET /api/admin ===
@Nested
@DisplayName("GET /api/admin")
class GetAllProductsTests {

    @Test
    @DisplayName("商品が存在する場合、商品リスト（ProductList）とHTTP 200を返す")
    void getAllProducts_WhenProductsExist_ShouldReturnProductList() throws Exception{
        //Arrange(setUpのデフォルトモックを使用)

        // Act & Assert
        mockMvc.perform(get("/api/admin")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // ステータスコード200 OK
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Content-TypeがJSON
                    .andExpect(jsonPath("$", hasSize(2))) // ルート配列のサイズが2
                    // 1番目の要素の全フィールドを検証
                    .andExpect(jsonPath("$[0].id", is(productListItem1.getId())))
                    .andExpect(jsonPath("$[0].name", is(productListItem1.getName())))
                    .andExpect(jsonPath("$[0].price", is(productListItem1.getPrice())))
                    .andExpect(jsonPath("$[0].salePrice", is(productListItem1.getSalePrice())))
                    .andExpect(jsonPath("$[0].description", is(productListItem1.getDescription())))
                    .andExpect(jsonPath("$[0].stockQuantity", is(productListItem1.getStockQuantity())))
                    .andExpect(jsonPath("$[0].imageUrl", is(productListItem1.getImageUrl())))
                    // 2番目の要素の全フィールドを検証
                    .andExpect(jsonPath("$[1].id", is(productListItem2.getId())))
                    .andExpect(jsonPath("$[1].name", is(productListItem2.getName())))
                    .andExpect(jsonPath("$[1].price", is(productListItem2.getPrice())))
                    .andExpect(jsonPath("$[1].salePrice", is(productListItem2.getSalePrice())))
                    .andExpect(jsonPath("$[1].description", is(productListItem2.getDescription())))
                    .andExpect(jsonPath("$[1].stockQuantity", is(productListItem2.getStockQuantity())))
                    .andExpect(jsonPath("$[1].imageUrl", is(productListItem2.getImageUrl())));

            verify(adminService, times(1)).findAllProducts();
            verifyNoMoreInteractions(adminService);
        }

        @Test
        @DisplayName("商品が存在しない場合、空のリストを200 OKで返す")
        void getAllProducts_WhenNoProductsExist_ShouldReturnEmptyList() throws Exception {
            // Arrange
            when(adminService.findAllProducts()).thenReturn(Collections.emptyList()); // 空リストを返すように設定

            // Act & Assert
            mockMvc.perform(get("/api/admin")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0))); // 空の配列であることを確認

            verify(adminService, times(1)).findAllProducts();
            verifyNoMoreInteractions(adminService);
        }

         @Test
        @DisplayName("AdminServiceが例外をスローした場合、500 Internal Server Errorを返す")
        void getAllProducts_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
            // Arrange
            when(adminService.findAllProducts()).thenThrow(new RuntimeException("サービスエラー"));

            // Act & Assert
            mockMvc.perform(get("/api/admin")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // GlobalExceptionHandler が有効ならエラーメッセージを含むJSONが返る可能性がある
                    .andExpect(jsonPath("$.message", containsString("サービスエラー")));

            verify(adminService, times(1)).findAllProducts();
            verifyNoMoreInteractions(adminService);
        }

        
    }

    // === GET /api/admin/{id} ===
    @Nested
    @DisplayName("GET /api/admin/{id}")
    class GetProductForAdminByIdTests {

        @Test
        @DisplayName("存在するidの場合、管理者用の商品詳細(ProductDetailForAdmin)を200 OKで返す")
        void getProductForAdminById_WhenProductExists_ShouldReturnProductDetailForAdmin() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:1)
            int id = 1;

            // Act & Assert
            mockMvc.perform(get("/api/admin/{id}", id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    // 全フィールドを検証
                    .andExpect(jsonPath("$.id", is(productDetailForAdmin1.getId())))
                    .andExpect(jsonPath("$.name", is(productDetailForAdmin1.getName())))
                    .andExpect(jsonPath("$.price", is(productDetailForAdmin1.getPrice())))
                    .andExpect(jsonPath("$.description", is(productDetailForAdmin1.getDescription())))
                    .andExpect(jsonPath("$.stockQuantity", is(productDetailForAdmin1.getStockQuantity())))
                    .andExpect(jsonPath("$.imageUrl", is(productDetailForAdmin1.getImageUrl())));

            verify(adminService, times(1)).findProductForAdminById(id);
            verifyNoMoreInteractions(adminService);
        }

        @Test
        @DisplayName("存在するidで、一部フィールドがnullの商品の場合、nullを含む商品詳細を200 OKで返す")
        void getProductById_WhenProductExistsWithNullFields_ShouldReturnProductDetailForAdminWithNulls() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:3)
            Integer productId = 3;

            // Act & Assert
            mockMvc.perform(get("/api/products/{productId}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(productDetailForAdminWithNulls.getId())))
                    .andExpect(jsonPath("$.name", is(productDetailForAdminWithNulls.getName())))
                    .andExpect(jsonPath("$.price", is(productDetailForAdminWithNulls.getPrice())))
                    .andExpect(jsonPath("$.description", is(nullValue()))) // descriptionがnull
                    .andExpect(jsonPath("$.stockQuantity", is(productDetailForAdminWithNulls.getStockQuantity())))
                    .andExpect(jsonPath("$.imageUrl", is(nullValue()))); // imageUrlがnull

            verify(adminService, times(1)).findProductForAdminById(productId);
            verifyNoMoreInteractions(adminService);
        }

        @Test
        @DisplayName("存在しないproductIdの場合、404 Not Foundを返す")
        void getProductById_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:99)
            int id = 99;

            // Act & Assert
            mockMvc.perform(get("/api/admin/{id}", id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()); // ステータスコード404 Not Found

            verify(adminService, times(1)).findProductForAdminById(id);
            verifyNoMoreInteractions(adminService);
        }

        @Test
        @DisplayName("idが数値でない場合、500 Internal Server Errorを返す (現在のGlobalExceptionHandlerの実装による)") // DisplayName を変更
        void getProductById_WithInvalidProductIdFormat_ShouldReturnInternalServerError_DueToExceptionHandler() throws Exception { // メソッド名を変更
            // Arrange
            String invalidId = "abc"; // 数値でないパスパラメータ

            // Act & Assert
            // 現在のGlobalExceptionHandlerは型ミスマッチをRuntimeExceptionとして扱い500を返すため、
            // テストの期待値もそれに合わせる。
            mockMvc.perform(get("/api/admin/{id}", invalidId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // オプション： GlobalExceptionHandlerが返すエラーメッセージの内容も検証する
                    .andExpect(jsonPath("$.message", containsString("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'")))
                    .andExpect(jsonPath("$.message", containsString(invalidId))); // 不正な値が含まれていることを確認

            // この場合、コントローラーメソッドやサービスは呼び出されない
            verifyNoInteractions(adminService);
        }

        @Test
        @DisplayName("AdminServiceが例外をスローした場合、500 Internal Server Errorを返す")
        void getProductForAdminById_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
            // Arrange
            int id = 1;
            when(adminService.findProductForAdminById(id)).thenThrow(new RuntimeException("サービスエラー"));

            // Act & Assert
            mockMvc.perform(get("/api/admin", id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // GlobalExceptionHandler が有効ならエラーメッセージを含むJSONが返る可能性がある
                    .andExpect(jsonPath("$.message", containsString("サービスエラー")));


            verify(adminService, times(1)).findProductForAdminById(id);
            verifyNoMoreInteractions(adminService);
        }
    }
}
