package com.example.raffinehome.product.exception;

<<<<<<< HEAD
public class ProductNotFoundException {
    
}
=======
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
>>>>>>> c701c0a4d878665a65c66999082773ddbcd087f5
