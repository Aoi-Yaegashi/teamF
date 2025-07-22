package com.example.raffinehome.admin.service;

import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.admin.dto.AdminDeleteDTO;
import com.example.raffinehome.admin.dto.AdminProductDto;
import com.example.raffinehome.admin.dto.AdminUpdateDTO;
import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    @Test
    void createProduct_WhenValidInput_ShouldSaveAndReturnProduct() {
        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        AdminService adminService = new AdminService(productRepository);

        AdminCreateDTO dto = new AdminCreateDTO(
            "テスト商品",
            1000,
            900,
            "テスト説明",
            50,
            "http://example.com/img.png"
        );

        // ProductRepository.save(product) の戻り値を模倣
        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setName(dto.getName());
        savedProduct.setDescription(dto.getDescription());
        savedProduct.setPrice(dto.getPrice());
        savedProduct.setSalePrice(dto.getSalePrice());
        savedProduct.setStockQuantity(dto.getStockQuantity());
        savedProduct.setImageUrl(dto.getImageUrl());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = adminService.createProduct(dto);

        // Assert
        // 1. saveが1回だけ呼ばれたか
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product passedProduct = productCaptor.getValue();
        assertEquals(dto.getName(), passedProduct.getName());
        assertEquals(dto.getDescription(), passedProduct.getDescription());
        assertEquals(dto.getPrice(), passedProduct.getPrice());
        assertEquals(dto.getSalePrice(), passedProduct.getSalePrice());
        assertEquals(dto.getStockQuantity(), passedProduct.getStockQuantity());
        assertEquals(dto.getImageUrl(), passedProduct.getImageUrl());

        // 2. 結果値の検証
        assertNotNull(result);
        assertEquals(savedProduct.getId(), result.getId());
        assertEquals(savedProduct.getName(), result.getName());
        assertEquals(savedProduct.getDescription(), result.getDescription());
        assertEquals(savedProduct.getPrice(), result.getPrice());
        assertEquals(savedProduct.getSalePrice(), result.getSalePrice());
        assertEquals(savedProduct.getStockQuantity(), result.getStockQuantity());
        assertEquals(savedProduct.getImageUrl(), result.getImageUrl());
    }

        @Test
    void createProduct_WhenOptionalFieldsNull_ShouldSaveAndReturnProduct() {
        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        AdminService adminService = new AdminService(productRepository);

        // name以外null（int型は0, String型はnullとする）
        AdminCreateDTO dto = new AdminCreateDTO(
            "テスト商品",     // name (必須)
            0,               // price（int型は0となる: Java仕様）
            0,               // salePrice
            null,            // description
            0,               // stockQuantity
            null             // imageUrl
        );

        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setName(dto.getName());
        savedProduct.setPrice(dto.getPrice());
        savedProduct.setSalePrice(dto.getSalePrice());
        savedProduct.setDescription(dto.getDescription());
        savedProduct.setStockQuantity(dto.getStockQuantity());
        savedProduct.setImageUrl(dto.getImageUrl());

        // ProductRepository.save()の戻り値模倣
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = adminService.createProduct(dto);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product passedProduct = productCaptor.getValue();
        // nameは必須、それ以外nullや0になること
        assertEquals(dto.getName(), passedProduct.getName());
        assertEquals(0, passedProduct.getPrice());
        assertEquals(0, passedProduct.getSalePrice());
        assertNull(passedProduct.getDescription());
        assertEquals(0, passedProduct.getStockQuantity());
        assertNull(passedProduct.getImageUrl());

        // 戻り値も同様
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(0, result.getPrice());
        assertEquals(0, result.getSalePrice());
        assertNull(result.getDescription());
        assertEquals(0, result.getStockQuantity());
        assertNull(result.getImageUrl());
    }

        @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        AdminService adminService = new AdminService(productRepository);

        int productId = 123;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldName");
        existingProduct.setDescription("OldDescription");
        existingProduct.setPrice(1000);
        existingProduct.setSalePrice(900);
        existingProduct.setStockQuantity(10);
        existingProduct.setImageUrl("old.png");

        // Mock: findByIdが該当Productを返す
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // 更新リクエスト
        AdminUpdateDTO updateDTO = new AdminUpdateDTO(
                productId,
                "NewName",
                "NewDescription",
                1100,
                950,
                15,
                "new.png"
        );

        // 保存されたProductの戻り値を返す
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName(updateDTO.getName());
        updatedProduct.setDescription(updateDTO.getDescription());
        updatedProduct.setPrice(updateDTO.getPrice());
        updatedProduct.setSalePrice(updateDTO.getSalePrice());
        updatedProduct.setStockQuantity(updateDTO.getStockQuantity());
        updatedProduct.setImageUrl(updateDTO.getImageUrl());
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = adminService.updateProduct(productId, updateDTO);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();

        assertEquals(updateDTO.getName(), saved.getName());
        assertEquals(updateDTO.getDescription(), saved.getDescription());
        assertEquals(updateDTO.getPrice(), saved.getPrice());
        assertEquals(updateDTO.getSalePrice(), saved.getSalePrice());
        assertEquals(updateDTO.getStockQuantity(), saved.getStockQuantity());
        assertEquals(updateDTO.getImageUrl(), saved.getImageUrl());

        // 返り値も同様
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getDescription(), result.getDescription());
        assertEquals(updateDTO.getPrice(), result.getPrice());
        assertEquals(updateDTO.getSalePrice(), result.getSalePrice());
        assertEquals(updateDTO.getStockQuantity(), result.getStockQuantity());
        assertEquals(updateDTO.getImageUrl(), result.getImageUrl());
    }

        @Test
    void updateProduct_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        AdminService adminService = new AdminService(productRepository);

        int productId = 999;
        AdminUpdateDTO updateDTO = new AdminUpdateDTO(
                productId,
                "name",
                "description",
                100,
                90,
                10,
                "img.png"
        );

        // ProductRepository#findById が Optional.empty() を返すようモック
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> adminService.updateProduct(productId, updateDTO)
        );
        assertEquals("該当商品が見つかりません", thrown.getMessage());
    }

        @Test
    void deleteProduct_WhenProductExists_ShouldMarkAsDeletedAndSave() {
        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        AdminService adminService = new AdminService(productRepository);

        int productId = 10;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setDeleted(false);

        // findByIdが該当Productを返すようにモック
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // save()で返すProduct（isDeleted=trueで返す）
        Product deletedProduct = new Product();
        deletedProduct.setId(productId);
        deletedProduct.setDeleted(true);
        when(productRepository.save(any(Product.class))).thenReturn(deletedProduct);

        // Act
        Product result = adminService.deleteProduct(productId);

        // Assert
        // isDeletedフラグがtrueになったか
        assertTrue(result.isDeleted());

        // saveの引数のProductもisDeletedがtrueになっているか
        verify(productRepository, times(1)).save(argThat(product ->
                product.getId() == productId && product.isDeleted()
        ));

        // findByIdが1回呼ばれたか
        verify(productRepository, times(1)).findById(productId);
    }

        @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        AdminService adminService = new AdminService(productRepository);

        int notFoundId = 999;

        // findByIdがOptional.empty()を返すモック
        when(productRepository.findById(notFoundId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> adminService.deleteProduct(notFoundId)
        );
        assertEquals("該当商品が見つかりません", thrown.getMessage());

        verify(productRepository, times(1)).findById(notFoundId);
        verify(productRepository, never()).save(any());
    }
}
