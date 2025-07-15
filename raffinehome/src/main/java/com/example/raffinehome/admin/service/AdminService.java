package com.example.raffinehome.admin.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.raffinehome.product.dto.ProductCreateDTO;
import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.repository.ProductRepository;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;

import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.admin.dto.AdminUpdateDTO;
import com.example.raffinehome.admin.dto.AdminDeleteDTO;

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

    public Product createProduct(Product request){
        
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

            return productRepository.save(product);
            
        } else {
            new IllegalStateException("該当商品が見つかりません" );
        // 商品が存在しない時の処理
        }
    }

    public Product updateProduct(int id){

        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            /*adminUpdateDTO.setDescription(updateRequest.getDescription());
            adminUpdateDTO.setPrice(updateRequest.getPrice());
            adminUpdateDTO.setSalePrice(updateRequest.getSalePrice());
            adminUpdateDTO.setStockQuantity(updateRequest.getStockQuantity());
            adminUpdateDTO.setImageUrl(updateRequest.getImageUrl());*/

            return productRepository.save(product);
        
        } else {
            new IllegalStateException("該当商品が見つかりません" );
        // 商品が存在しない時の処理
        }  
    }
}

/*Productのどの要素に作用するかをどの記述で決めてる？*/