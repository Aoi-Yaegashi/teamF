package com.example.raffinehome.product.exception;

/**
 * 完全に在庫切れの場合にスローされる例外
 */
public class OutOfStockException extends RuntimeException {
    private final int productId;
    private final String productName;

    /**
     * コンストラクタ
     * @param productId 商品ID
     * @param productName 商品名
     */
    public OutOfStockException(int productId, String productName) {
        super(String.format("商品「%s」(ID:%d) は在庫切れです。", productName, productId));
        this.productId = productId;
        this.productName = productName;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}