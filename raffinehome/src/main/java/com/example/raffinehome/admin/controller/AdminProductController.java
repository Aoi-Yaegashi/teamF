package com.example.raffinehome.admin.controller;

import com.example.raffinehome.admin.service.AdminProductImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductImportService importService;

    @Autowired
    public AdminProductController(AdminProductImportService importService) {
        this.importService = importService;
    }

    /**
     * 管理者によるCSVインポート（カウント表示なし）
     */
    @PostMapping("/import")
    public ResponseEntity<?> importCsv(@RequestParam("csv") MultipartFile file,
                                       @RequestParam("mapping") String mappingType) {
        try {
            // 件数などを返さず、成功したらメッセージだけ返す
            importService.importCsv(file, mappingType);
            return ResponseEntity.ok("インポートが完了しました");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("インポートに失敗しました: " + e.getMessage());
        }
    }
}
