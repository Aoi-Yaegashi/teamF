package com.example.raffinehome.product.exception;

/**
 * 指定した商品が見つからない場合にスローされる例外
 */
public class ProductNotFoundException extends RuntimeException {
    private final int productId;

    /**
     * コンストラクタ
     * @param productId 商品ID
     */
    public ProductNotFoundException(int productId) {
        super(String.format("商品が見つかりません (ID:%d)", productId));
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }
}