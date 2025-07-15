package com.example.raffinehome.admin.service;

import com.example.raffinehome.product.entity.Product;
import com.example.raffinehome.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
            int successCount = 0;

            for (CSVRecord record : csvParser) {
                try {
                    // CSVの各列を取得
                    String name = record.get("name");
                    int price = Integer.parseInt(record.get("price"));
                    int stock = Integer.parseInt(record.get("stock_quantity"));
                    String description = record.get("description");
                    String imageUrl = record.get("image_url");

                    // Productを新規作成＆保存
                    Product product = new Product();
                    product.setName(name);
                    product.setPrice(price);
                    product.setStockQuantity(stock);
                    product.setDescription(description);
                    product.setImageUrl(imageUrl);

                    productRepository.save(product);
                    successCount++;

                } catch (Exception e) {
                    // 行番号付きでログ出力（record.getRecordNumber() は1開始）
                    System.err.println("⚠ CSVの行[" + record.getRecordNumber() + "] でエラーが発生しました: " + e.getMessage());
                    // → production環境では Logger.warn などが望ましい
                }
            }

            return successCount;
        } catch (Exception e) {
            // CSVファイル全体の読み込みが失敗した場合
            System.err.println("CSVファイル全体の読み込みに失敗: " + e.getMessage());
            throw e;
        }
    }
}