package com.example.raffinehome.product.exception;

<<<<<<< HEAD
public class InsufficientStockException {
    
}
=======
/**
 * 在庫不足時にスローされる例外
 */
public class InsufficientStockException extends RuntimeException {
    private final int productId;
    private final String productName;
    private final int requestedQuantity;
    private final int availableQuantity;

    /**
     * コンストラクタ
     * @param productId 商品ID
     * @param productName 商品名
     * @param requestedQuantity 要求数量
     * @param availableQuantity 在庫数量
     */
    public InsufficientStockException(int productId, String productName, int requestedQuantity, int availableQuantity) {
        super(String.format(
            "商品「%s」(ID:%d) の在庫が不足しています。要求数: %d, 在庫数: %d",
            productName, productId, requestedQuantity, availableQuantity
        ));
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
>>>>>>> c701c0a4d878665a65c66999082773ddbcd087f5
