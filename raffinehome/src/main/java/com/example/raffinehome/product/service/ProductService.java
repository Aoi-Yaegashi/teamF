package com.example.raffinehome.product.service;

import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.dto.ProductListDTO;
import com.example.raffinehome.product.dto.ProductUpdateDTO;
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
    
    public List<ProductListDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    
    public ProductDTO findProductById(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::convertToDetail).orElse(null);
    }

    private ProductDTO convertToDetail(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getSalePrice(),
            product.getDescription(),
            product.getStockQuantity(),
            product.getImageUrl(),
            product.getStockQuantity() > 0
        );
    }
    
    private ProductListDTO convertToListItem(Product product) {
        return new ProductListDTO(
                product.getName(),
                product.getPrice(),
                product.getSalePrice(),
                product.getDescription(),
                product.getStockQuantity(),
                product.getImageUrl()
        ) ;
    }
    
}