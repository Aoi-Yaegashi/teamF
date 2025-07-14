import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.raffinehome.product.dto.ProductDetail;
import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.service.ProductService;
import com.example.raffinehome.product.dto.ProductListItem;
import com.example.raffinehome.admin.dto.AdminCreateDTO;
import com.example.raffinehome.admin.dto.AdminUpdateDTO;

import java.util.List;

@RestController
@RequestMapping("/api/admin")

public class AdminController{

    private final ProductService productService;

    @Autowired
    public AdminController(ProductService productService){
    this.productService = productService;
}

@GetMapping
public ResponseEntity<List<ProductListItem>> getAllProducts(){
    List<ProductListItem> products = productService.findAllProducts();
    return ResponseEntity.ok(products);
}

@GetMapping("/{productId}")
public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId){
    ProductDetail product = productService.findProductById(productId);
    if (product == null){
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(product);
}

@PostMapping
public ResponseEntity<String> createProduct(@RequestBody AdminCreateDTO dto) {
    return ResponseEntity.ok("商品登録が完了しました");
}

@PutMapping("/{productId}")
public ResponseEntity<String> updateProduct(@RequestBody AdminUpdateDTO dto){
    return ResponseEntity.ok("商品情報を更新しました");
}
}
