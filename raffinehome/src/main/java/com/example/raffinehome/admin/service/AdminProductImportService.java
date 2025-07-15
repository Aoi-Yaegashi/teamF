package com.example.raffinehome.admin.service;

import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Service
public class AdminProductImportService {

    private final ProductRepository productRepository;

    public AdminProductImportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int importCsv(MultipartFile file, String mappingType) throws Exception {
        try (
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            int count = 0;
            for (CSVRecord record : csvParser) {
                // mappingType によって処理を分岐可能（在庫だけ更新、価格だけ更新など）

                String name = record.get("name");
                int price = Integer.parseInt(record.get("price"));
                int stock = Integer.parseInt(record.get("stock_quantity"));
                String description = record.get("description");
                String imageUrl = record.get("image_url");

                Product product = new Product();
                product.setName(name);
                product.setPrice(price);
                product.setStockQuantity(stock);
                product.setDescription(description);
                product.setImageUrl(imageUrl);

                productRepository.save(product);

                count++;
            }
            return count;
        }
    }
}