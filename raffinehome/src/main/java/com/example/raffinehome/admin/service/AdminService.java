package com.example.raffinehome.admin.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.repository.ProductRepository;
import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.product.entity.Product;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;

import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.admin.dto.AdminUpdateDTO;
import com.example.raffinehome.admin.dto.AdminDeleteDTO;

    // 追加　by K.K
import com.example.raffinehome.admin.dto.AdminProductDto;

import lombok.Data;

import com.example.raffinehome.product.entity.Product;

@Service
@Transactional
public class AdminService {
    @Autowired
    private ProductRepository productRepository;

    public AdminService(ProductRepository productRepository) {
    this.productRepository = productRepository;
    }

    // 追加　by K.K
    public List<ProductListDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    // 追加　by K.K
    private ProductListDTO convertToListItem(Product product) {
        return new ProductListDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getSalePrice(),
                product.getDescription(),
                product.getStockQuantity(),
                product.getImageUrl()
        ) ;
    }
    // 追加　by K.K
    public AdminProductDto findProductForAdminById(int id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::convertToDetail).orElse(null);
    }
    // 追加　by K.K
    private AdminProductDto convertToDetail(Product product) {
        AdminProductDto dto = new AdminProductDto();
        dto.setId(product.getId());    
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());  
        dto.setSalePrice(product.getSalePrice());  
        dto.setDescription(product.getDescription());
        dto.setStockQuantity(product.getStockQuantity());   
        dto.setImageUrl(product.getImageUrl()); 
        dto.setDeleted(product.isDeleted());
        dto.setCreatedAt(product.getCreatedAt());  
        dto.setUpdatedAt(product.getUpdatedAt());    
            
        return dto;
    }

    public Product createProduct(AdminCreateDTO request){
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSalePrice(request.getSalePrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());

        return productRepository.save(product);
    }

    public Product deleteProduct(int id){

        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            // productの処理
            product.setDeleted(true);
            return productRepository.save(product);
            
        } else {
            throw new IllegalStateException("該当商品が見つかりません" );
        // 商品が存在しない時の処理
        }
    }

    public Product updateProduct(int id,AdminUpdateDTO updateRequest){


        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setName(updateRequest.getName());
            product.setDescription(updateRequest.getDescription());
            product.setPrice(updateRequest.getPrice());
            product.setSalePrice(updateRequest.getSalePrice());
            product.setStockQuantity(updateRequest.getStockQuantity());
            product.setImageUrl(updateRequest.getImageUrl());

            return productRepository.save(product);
        
        } else {
            throw new IllegalStateException("該当商品が見つかりません" );
        // 商品が存在しない時の処理
        }  
    }
}