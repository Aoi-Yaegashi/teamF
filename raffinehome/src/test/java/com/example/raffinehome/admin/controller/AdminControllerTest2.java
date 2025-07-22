package com.example.raffinehome.admin.controller;

import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.exception.ProductExceptionHandler;
import com.example.raffinehome.product.service.ProductService;
import com.example.raffinehome.order.dto.OrderDTO;
import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.admin.dto.AdminProductDto;
import com.example.raffinehome.admin.dto.AdminUpdateDTO;
import com.example.raffinehome.admin.service.AdminService;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(AdminController.class)
class AdminControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AdminService adminService;

    @Test
    @DisplayName("GET /api/admin - 商品が存在する場合200、リスト返却")
    void getAllProducts_WhenProductsExist_ShouldReturnProductList() throws Exception {
        // モック用データ
        ProductListDTO dto1 = new ProductListDTO(
                1, "商品A", 1000, 900, "説明A", 10, "imageA.jpg"
        );
        ProductListDTO dto2 = new ProductListDTO(
                2, "商品B", 2000, 1800, "説明B", 5, "imageB.jpg"
        );
        List<ProductListDTO> mockProducts = Arrays.asList(dto1, dto2);

        // productService.findAllProducts がモックデータを返すよう設定
        when(adminService.findAllProducts()).thenReturn(mockProducts);

        // 検証
        mockMvc.perform(get("/api/admin")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("商品A")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("商品B")));
    }


    @Test
    @DisplayName("GET /api/admin - 商品が存在しない場合 空リスト＆200OK")
    void getAllProducts_WhenNoProductsExist_ShouldReturnEmptyList() throws Exception {
        // 空リストを返すようにモック
        when(productService.findAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/admin/{id} - 存在するidに対し詳細取得 正常 200")
    void getProductForAdminById_WhenProductExists_ShouldReturnProductDetailForAdmin() throws Exception {
        int productId = 101;

        AdminProductDto productDto = new AdminProductDto(
                productId,
                "テスト商品",
                "商品説明テキスト",
                1234,
                1200,
                20,
                false,
                "sample.png",
                LocalDateTime.of(2024, 5, 10, 12, 0),
                LocalDateTime.of(2024, 5, 11, 13, 0)
        );

        when(adminService.findProductForAdminById(productId)).thenReturn(productDto);

        mockMvc.perform(get("/api/admin/{id}", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(productId))
            .andExpect(jsonPath("$.name").value("テスト商品"))
            .andExpect(jsonPath("$.description").value("商品説明テキスト"))
            .andExpect(jsonPath("$.price").value(1234))
            .andExpect(jsonPath("$.salePrice").value(1200))
            .andExpect(jsonPath("$.stockQuantity").value(20))
            .andExpect(jsonPath("$.deleted").value(false))
            .andExpect(jsonPath("$.imageUrl").value("sample.png"))
            ; // createdAt, updatedAt なども必要に応じ検証
    }

    @Test
    @DisplayName("GET /api/admin/{id} - 存在しないidを指定した場合404を返す")
    void getProductForAdminById_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // 存在しないID
        int nonExistentId = 999;

        // AdminServiceがnull返却をモック
        when(adminService.findProductForAdminById(nonExistentId)).thenReturn(null);

        mockMvc.perform(get("/api/admin/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/admin - 有効な登録内容で商品登録成功・200OK")
    void createProduct_WhenValidInput_ShouldReturnSuccess() throws Exception {
        // 入力用リクエストJSON
        String requestJson = """
            {
                "name": "新商品",
                "price": 2000,
                "salePrice": 1800,
                "description": "説明テキスト",
                "stockQuantity": 12,
                "imageUrl": "sample.jpg"
            }
            """;

        // 戻り値となるProductエンティティをダミー生成（返却値は使わないが、モックとしてセット）
        Product dummyProduct = new Product();
        dummyProduct.setName("新商品");
        dummyProduct.setPrice(2000);
        dummyProduct.setSalePrice(1800);
        dummyProduct.setDescription("説明テキスト");
        dummyProduct.setStockQuantity(12);
        dummyProduct.setImageUrl("sample.jpg");

        // createProductが呼ばれたらdummyProductを返すように
        when(adminService.createProduct(any(AdminCreateDTO.class))).thenReturn(dummyProduct);

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().string("商品登録が完了しました"));
    }

    @Test
    @DisplayName("POST /api/admin - nameがnullの場合400+バリデーションメッセージ")
    void createProduct_WithNullCustomerInfo_ShouldReturnBadRequest() throws Exception {
        String requestJson = """
            {
                "name": null,
                "price": 2000,
                "salePrice": 1800,
                "description": "説明",
                "stockQuantity": 2,
                "imageUrl": "a.jpg"
            }
            """;

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("商品名は必須です"))); // @NotBlankのmessage

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /api/admin priceが0のとき400＋バリデーションメッセージ")
    void createProduct_WithZeroPrice_ShouldReturnBadRequest() throws Exception {
    String requestJson = """
        {
            "name": "新商品",
            "price": 0,
            "salePrice": 1500,
            "description": "サンプル説明",
            "stockQuantity": 10,
            "imageUrl": "image.png"
        }
        """;

    mockMvc.perform(post("/api/admin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("価格は1以上で入力してください")));

    verifyNoInteractions(adminService);
}

    @Test
    @DisplayName("POST /api/admin price不正な形式→400＋バリデーションメッセージ")
    void createProduct_WithInvalidPriceFormat_ShouldReturnBadRequest() throws Exception {
        // priceに数値でない文字列（例："abc"）
        String requestJson = """
            {
                "name": "サンプル商品",
                "price": "abc",
                "salePrice": 1500,
                "description": "説明説明",
                "stockQuantity": 5,
                "imageUrl": "img.jpg"
            }
            """;

        mockMvc.perform(post("/api/admin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("price")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("priceは正しい数値で指定してください。")));

    // サービス層呼び出しがないことを検証
    verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /api/admin salePriceが不正な形式だと400＋バリデーションメッセージ")
    void createProduct_WithInvalidSalePriceFormat_ShouldReturnBadRequest() throws Exception {
        String requestJson = """
            {
                "name": "新商品",
                "price": 1000,
                "salePrice": "abc",
                "description": "セール対象商品",
                "stockQuantity": 10,
                "imageUrl": "image.png"
            }
            """;

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            // "salePrice"というフィールド名が含まれるか、エラー内容が適切かどうかを確認
            .andExpect(content().string(org.hamcrest.Matchers.containsString("salePrice")))
            // ↓ エラーメッセージ例（調整ください。型変換失敗の場合この限りではありません）
            //.andExpect(content().string(org.hamcrest.Matchers.containsString("値が正しくありません")))
            ;

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /api/admin stockQuantityが不正な形式の場合400＋バリデーションエラー")
    void createProduct_WithInvalidStockQuantityFormat_ShouldReturnBadRequest() throws Exception {
        String requestJson = """
            {
                "name": "商品B",
                "price": 2000,
                "salePrice": 1800,
                "description": "説明B",
                "stockQuantity": "abc",
                "imageUrl": "b.jpg"
            }
            """;

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            // "stockQuantity"のエラー発生を確認。詳細なメッセージは実装に合わせて調整下さい。
            .andExpect(content().string(org.hamcrest.Matchers.containsString("stockQuantity")));

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("PUT /api/admin/{id} 存在するidを指定した場合 200OK")
    void updateProduct_WhenIdExists_SholdReturnBadRequest() throws Exception {
        int id = 10;

        String requestJson = """
            {
                "id": 10,
                "name": "更新商品",
                "description": "更新説明",
                "price": 3000,
                "salePrice": 2800,
                "stockQuantity": 15,
                "imageUrl": "updated.jpg"
            }
            """;

        AdminUpdateDTO updateDto = new AdminUpdateDTO(
            10,
            "更新商品",
            "更新説明",
            3000,
            2800,
            15,
            "updated.jpg"
        );

        Product dummyProduct = new Product(); // 実際には返り値はassert不要。モック戻り値用

        when(adminService.updateProduct(eq(id), any(AdminUpdateDTO.class))).thenReturn(dummyProduct);

        mockMvc.perform(put("/api/admin/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().string("商品情報を更新しました"));

        // updateProductが1回だけ呼ばれたこと＋引数チェック
        ArgumentCaptor<AdminUpdateDTO> dtoCaptor = ArgumentCaptor.forClass(AdminUpdateDTO.class);
        verify(adminService, times(1)).updateProduct(eq(id), dtoCaptor.capture());
        AdminUpdateDTO passedDto = dtoCaptor.getValue();
        assertThat(passedDto.getName()).isEqualTo("更新商品");
        assertThat(passedDto.getPrice()).isEqualTo(3000);
    }

    @Test
    @DisplayName("PUT /api/admin/{id} 存在しないid指定時は500エラー返却")
    void updateProduct_WhenProductNotFound_ShouldReturnError() throws Exception {
        int id = 999;
        String requestJson = """
            {
                "id": 999,
                "name": "NotFound商品",
                "description": "NotFound説明",
                "price": 100,
                "salePrice": 90,
                "stockQuantity": 1,
                "imageUrl": "none.jpg"
            }
            """;

        // IllegalStateExceptionを投げるようにモック
        when(adminService.updateProduct(anyInt(), any(AdminUpdateDTO.class)))
            .thenThrow(new IllegalStateException("該当商品が見つかりません"));

        mockMvc.perform(put("/api/admin/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("DELETE /api/admin/{id} 存在するid指定時に削除成功・200OK")
    void deleteProduct_WhenProductExists_ShouldReturnSuccess() throws Exception {
        int id = 55;

        // 消去時の戻り値（ダミーProductでOK）
        Product dummyProduct = new Product();
        dummyProduct.setId(id);

        when(adminService.deleteProduct(id)).thenReturn(dummyProduct);

        mockMvc.perform(delete("/api/admin/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("商品を削除しました"));

        // サービスの呼び出しも1回確認
        verify(adminService).deleteProduct(id);
    }

    @Test
    @DisplayName("DELETE /api/admin/{id} 存在しないid時に500エラー返却")
    void deleteProduct_WhenProductNotFound_ShouldReturnError() throws Exception {
        int id = 999;

        when(adminService.deleteProduct(anyInt()))
            .thenThrow(new IllegalStateException("該当商品が見つかりません"));

        mockMvc.perform(delete("/api/admin/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }
}
