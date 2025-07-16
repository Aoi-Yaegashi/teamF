package com.example.raffinehome.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.raffinehome.product.dto.ProductDTO;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.service.ProductService;
import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.admin.dto.AdminUpdateDTO;
import com.example.raffinehome.admin.service.AdminService;
import com.example.raffinehome.product.dto.ProductListDTO;

import java.util.List;

@RestController
@RequestMapping("/api/admin")

public class AdminController{

    private final ProductService productService;
    private final AdminService adminService;

    @Autowired
    public AdminController(ProductService productService, AdminService adminService){
    this.productService = productService;
    this.adminService = adminService;
}

@GetMapping
public ResponseEntity<List<ProductListDTO>> getAllProducts(){
    List<ProductListDTO> products = productService.findAllProducts();
    return ResponseEntity.ok(products);
}

@GetMapping("/{id}")
public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer productId){
    ProductDTO product = productService.findProductById(productId);
    if (product == null){
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(product);
}

@PostMapping
public ResponseEntity<String> createProduct(@RequestBody AdminCreateDTO dto) {
    adminService.createProduct(dto);
    return ResponseEntity.ok("商品登録が完了しました");
}

@PutMapping("/{id}")
public ResponseEntity<String> updateProduct(@PathVariable("id")int id, @RequestBody AdminUpdateDTO dto){
    adminService.updateProduct(id, dto);
    return ResponseEntity.ok("商品情報を更新しました");
}

@DeleteMapping("/{id}")
public ResponseEntity<String> deleteProduct(@PathVariable("id")int id) {
    adminService.deleteProduct(id);
    return ResponseEntity.ok("商品を削除しました");
}
}
