package com.example.raffinehome.product.service;

import com.example.raffinehome.product.dto.ProductDetail;
import com.example.raffinehome.product.dto.ProductListItem;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository ProductRepository) {
        this.productRepository = ProductRepository;
    }
    
    public List<ProductListItem> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    
    public ProductDetail findProductById(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::convertToDetail).orElse(null);
    }
    
    private ProductListItem convertToListItem(Product product) {
        return new ProductListItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getSalePrice(),
                product.getImageUrl()
        );
    }
    
    private ProductDetail convertToDetail(Product product) {
        return new ProductDetail(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getSalePrice(),
                product.getDescription(),
                product.getStockQuantity(),
                product.getImageUrl()
        );
    }
}