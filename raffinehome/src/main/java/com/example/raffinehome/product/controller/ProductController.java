package com.example.raffinehome.product.controller;

<<<<<<< HEAD
public class ProductController {
    
}
=======
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
        List<ProductListDTO> product = productService.findAllProducts();
        return ResponseEntity.ok(product);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        ProductDTO product = productService.findProductById(id);
        if (product == null) {
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
>>>>>>> c701c0a4d878665a65c66999082773ddbcd087f5
