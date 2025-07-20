package com.example.raffinehome.product.service;

import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections; // 空のリスト用
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple; // tupleを使った検証用
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Product productWithNullFields; // nullフィールドを持つテストデータ

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1);
        product1.setName("商品1");
        product1.setPrice(100);
        product1.setImageUrl("/img1.png");
        product1.setDescription("説明1");
        product1.setStockQuantity(10);
        // createdAt, updatedAt はエンティティ側で自動設定される想定

        product2 = new Product();
        product2.setId(2);
        product2.setName("商品2");
        product2.setPrice(200);
        product2.setImageUrl("/img2.png");
        product2.setDescription("説明2");
        product2.setStockQuantity(5);

        productWithNullFields = new Product();
        productWithNullFields.setId(3);
        productWithNullFields.setName("商品3（Nullあり）");
        productWithNullFields.setPrice(300);
        productWithNullFields.setStockQuantity(8);
        productWithNullFields.setDescription(null); // descriptionがnull
        productWithNullFields.setImageUrl(null);    // imageUrlがnull
    }

    // === findAllProducts のテスト ===

    @Test
    @DisplayName("findAllProducts: リポジトリから複数の商品が返される場合、ProductListItemのリストを返す")
    void findAllProducts_ShouldReturnListOfProductListItems() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);

        // Act: テスト対象メソッドの実行
        List<ProductListDTO> result = productService.findAllProducts();

        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListDTO::getId, ProductListDTO::getName, ProductListDTO::getPrice, ProductListDTO::getImageUrl)
            .containsExactlyInAnyOrder(
                tuple(product1.getId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );

        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }

    @Test
    @DisplayName("findAllProducts: リポジトリから空のリストが返される場合、空のリストを返す")
    void findAllProducts_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductListDTO> result = productService.findAllProducts();

        // Assert
        assertThat(result).isEmpty();

        // Verify
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findAllProducts: 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
    void findAllProducts_WhenProductHasNullFields_ShouldMapNullToDto() {
        // Arrange
        List<Product> productsFromRepo = List.of(productWithNullFields);
        when(productRepository.findAll()).thenReturn(productsFromRepo);

        // Act
        List<ProductListDTO> result = productService.findAllProducts();

        // Assert
        assertThat(result).hasSize(1);
        ProductListDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(productWithNullFields.getId());
        assertThat(dto.getName()).isEqualTo(productWithNullFields.getName());
        assertThat(dto.getPrice()).isEqualTo(productWithNullFields.getPrice());
        assertThat(dto.getImageUrl()).isNull(); // imageUrlがnullであることを確認

        // Verify
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    // === findProductById のテスト ===

    @Test
    @DisplayName("findProductById: 存在するIDで検索した場合、ProductDetailを返す")
    void findProductById_WhenProductExists_ShouldReturnProductDetail() {
        // Arrange
        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        ProductDTO result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        // 全フィールドが正しくマッピングされているか検証
        assertThat(result.getId()).isEqualTo(product1.getId());
        assertThat(result.getName()).isEqualTo(product1.getName());
        assertThat(result.getPrice()).isEqualTo(product1.getPrice());
        assertThat(result.getDescription()).isEqualTo(product1.getDescription());
        assertThat(result.getStockQuantity()).isEqualTo(product1.getStockQuantity());
        assertThat(result.getImageUrl()).isEqualTo(product1.getImageUrl());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 存在しないIDで検索した場合、nullを返す")
    void findProductById_WhenProductNotExists_ShouldReturnNull() {
        // Arrange
        Integer productId = 99;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ProductDTO result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNull();

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findAllActiveProducts_ShouldReturnListOfProductListItems() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("商品A");
        product1.setPrice(1000);
        product1.setSalePrice(900);
        product1.setDescription("説明A");
        product1.setStockQuantity(10);
        product1.setImageUrl("urlA");
        // ...必要なら isDeleted フィールドを設定 (仮に false assumed)

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("商品B");
        product2.setPrice(2000);
        product2.setSalePrice(1900);
        product2.setDescription("説明B");
        product2.setStockQuantity(5);
        product2.setImageUrl("urlB");
        // ...必要なら isDeleted フィールドを設定 (仮に false assumed)

        List<Product> mockedList = Arrays.asList(product1, product2);

        when(productRepository.findAllActiveProducts())
            .thenReturn(mockedList);

        // Act
        List<ProductListDTO> result = productService.findAllActiveProducts();

        // Assert
        verify(productRepository, times(1)).findAllActiveProducts();

        assertThat(result).hasSize(2);

        assertThat(result.get(0)).extracting(
                ProductListDTO::getId,
                ProductListDTO::getName,
                ProductListDTO::getPrice,
                ProductListDTO::getSalePrice,
                ProductListDTO::getDescription,
                ProductListDTO::getStockQuantity,
                ProductListDTO::getImageUrl
        ).containsExactly(
                product1.getId(),
                product1.getName(),
                product1.getPrice(),
                product1.getSalePrice(),
                product1.getDescription(),
                product1.getStockQuantity(),
                product1.getImageUrl()
        );

        assertThat(result.get(1)).extracting(
                ProductListDTO::getId,
                ProductListDTO::getName,
                ProductListDTO::getPrice,
                ProductListDTO::getSalePrice,
                ProductListDTO::getDescription,
                ProductListDTO::getStockQuantity,
                ProductListDTO::getImageUrl
        ).containsExactly(
                product2.getId(),
                product2.getName(),
                product2.getPrice(),
                product2.getSalePrice(),
                product2.getDescription(),
                product2.getStockQuantity(),
                product2.getImageUrl()
        );
    }

    @Test
    void findAllActiveProducts_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAllActiveProducts()).thenReturn(Collections.emptyList());

        // Act
        List<ProductListDTO> result = productService.findAllActiveProducts();

        // Assert
        verify(productRepository, times(1)).findAllActiveProducts();
        assertThat(result).isEmpty(); // Collections.emptyList()とも比較可能
    }


    //全件検索において、論理削除済みのレコードを除外するテスト
    @Test
    @DisplayName("論理削除済みを除外してDTOリスト返す")
    void findAllActiveProducts_ShouldReturnOnlyActiveProducts() {
    // Arrange
    Product active = new Product();
    active.setId(1);
    active.setName("有効");
    active.setDeleted(false); // 論理削除フラグ（無効=有効）

    Product deleted = new Product();
    deleted.setId(2);
    deleted.setName("論理削除済");
    deleted.setDeleted(true); // 論理削除フラグ

    // findAllActiveProductsは「deleted=false」なエンティティだけ返すことを想定
    when(productRepository.findAllActiveProducts()).thenReturn(List.of(active));

    // Act
    List<ProductListDTO> result = productService.findAllActiveProducts();

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(active.getId());
    assertThat(result.get(0).getName()).isEqualTo(active.getName());

    verify(productRepository, times(1)).findAllActiveProducts();
}

    //論理削除レコードの非返却テスト
    @Test
    void findAllActive_ShouldNotReturnDeletedRecords() {
    // Arrange
    Product active = new Product();
    active.setId(1);
    active.setName("有効");
    active.setDeleted(false);
    // 他のフィールドも必要ならセット

    Product deleted = new Product();
    deleted.setId(2);
    deleted.setName("論理削除済");
    deleted.setDeleted(true);
    // 他のフィールドも必要ならセット

    // リポジトリのfindAllActiveProductsが削除済みを除外して返す前提
    when(productRepository.findAllActiveProducts()).thenReturn(Arrays.asList(active)); // 削除済を除外

    // Act
    List<ProductListDTO> result = productService.findAllActiveProducts();

    // Assert
    assertThat(result)
            .extracting(ProductListDTO::getId)
            .containsOnly(1);

    verify(productRepository, times(1)).findAllActiveProducts();
}

//以下、削除対象テスト
    @Test
    @DisplayName("findProductById: 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
    void findProductById_WhenProductHasNullFields_ShouldMapNullToDto() {
        // Arrange
        Integer productId = 3;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productWithNullFields));

        // Act
        ProductDTO result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productWithNullFields.getId());
        assertThat(result.getName()).isEqualTo(productWithNullFields.getName());
        assertThat(result.getPrice()).isEqualTo(productWithNullFields.getPrice());
        assertThat(result.getStockQuantity()).isEqualTo(productWithNullFields.getStockQuantity());
        assertThat(result.getDescription()).isNull(); // descriptionがnullであることを確認
        assertThat(result.getImageUrl()).isNull();    // imageUrlがnullであることを確認

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 引数productIdがnullの場合、リポジトリのfindById(null)が呼ばれ、結果的に例外またはnullが返る")
    void findProductById_WhenProductIdIsNull_ShouldDelegateToRepositoryAndPotentiallyFail() {
        // Arrange
        Integer nullProductId = null;
        // findById(null) がどのように振る舞うかはリポジトリの実装やJPAプロバイダに依存する。
        // ここでは、MockitoがfindById(null)を受け付け、Optional.empty() を返すように定義してみる。
        // (実際には NullPointerException や IllegalArgumentException が発生する可能性もある)
        when(productRepository.findById(nullProductId)).thenReturn(Optional.empty());
        // もし例外を期待する場合は以下のように書く
        // when(productRepository.findById(nullProductId)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // Act
        ProductDTO result = productService.findProductById(nullProductId);

        // Assert
        // Optional.empty() を返すように定義したので、結果は null になるはず
        assertThat(result).isNull();
        // 例外を期待する場合は以下のように書く
        // assertThatThrownBy(() -> productService.findProductById(nullProductId))
        //     .isInstanceOf(IllegalArgumentException.class)
        //     .hasMessage("ID cannot be null");

        // Verify
        verify(productRepository, times(1)).findById(nullProductId);
        verifyNoMoreInteractions(productRepository);
    }
}