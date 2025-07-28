package com.example.raffinehome.product.controller;

import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.dto.ProductListDTO;
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
    public ResponseEntity<List<ProductListDTO>> getAllProducts() {
 //        List<ProductListDTO> product = productService.findAllProducts();   
     // 追加　by K.K    
        List<ProductListDTO> product = productService.findAllActiveProducts();
        return ResponseEntity.ok(product);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        ProductDTO product = productService.findProductById(id);
        if (product == null || product.isDeleted() == true) {
            // 商品が存在しない、または削除されている場合は404を返す
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}