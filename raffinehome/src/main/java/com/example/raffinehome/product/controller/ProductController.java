package com.example.raffinehome.product.controller;

import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.dto.ProductCreateDTO;
import com.example.raffinehome.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService ProductService) {
        this.productService = ProductService;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> product = productService.findAllProducts();
        return ResponseEntity.ok(product);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        ProductDTO products = productService.findProductById(id);
        if (products == null) {
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }
}