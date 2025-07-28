package com.example.raffinehome.config;

import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        loadSampleProducts();
    }

    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            return; // すでにデータが存在する場合はスキップ
        }

        List<Product> products = Arrays.asList(
            createProduct(
                "シンプルデスクオーガナイザー", 
                3500, 
                3150,
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 
                20, 
                "/images/desk-organizer.png", 
                false
            ),
            createProduct(
                "アロマディフューザー", 
                4200, 
                3780,
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 
                15, 
                "/images/aroma-diffuser.png", 
                false
            ),
            createProduct(
                "コットンブランケット", 
                5800, 
                4500,
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 
                10, 
                "/images/cotton-blanket.png", 
                false
            ),
            createProduct(
                "ステンレスタンブラー", 
                2800, 
                2520,
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 
                30, 
                "/images/tumbler.png", 
                false
            ),
            createProduct(
                "ミニマルウォールクロック", 
                3200,
                2880, 
                "余計な装飾のないシンプルな壁掛け時計。静音設計。", 
                25, 
                "/images/wall-clock.png", 
                false
            ),
            createProduct(
                "リネンクッションカバー", 
                2500, 
                2250,
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 
                40, 
                "/images/cushion-cover.png", 
                true
            ),
            createProduct(
                "陶器フラワーベース", 
                4000, 
                3780,
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 
                15, 
                "/images/flower-vase.png", 
                false
            ),
            createProduct(
                "木製コースター（4枚セット）", 
                1800, 
                1620,
                "天然木を使用したシンプルなデザインのコースター。4枚セット。", 
                50, 
                "/images/wooden-coaster.png", 
                false
            ),
            createProduct(
                "キャンバストートバッグ", 
                3600, 
                3240,
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 
                35, 
                "/images/tote-bag.png", 
                true
            ),
            createProduct(
                "ガラス保存容器セット", 
                4500, 
                4050,
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。", 
                20, 
                "/images/glass-container.png", 
                false
            )
        );
        
        productRepository.saveAll(products);
    }
    
    private Product createProduct(String name, Integer price, Integer saleprice, String description,  Integer stock, String imageUrl, Boolean isDeleted) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setSalePrice(saleprice);
        product.setStockQuantity(stock);
        product.setImageUrl(imageUrl);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}