## 5. データ設計
### 5.1 概念データモデル（ER図）

<div class="mermaid">
erDiagram
    PRODUCT {
        int id PK "商品ID"
        string name "商品名"
        int price "販売価格"
        int salePrice "セール価格"
        string description "商品の詳細説明"
        int stockQuantity "在庫数"
        string image_url "商品画像のパスまたはURL"
        boolean isInStock "在庫の有無"
        boolean isDeleted "論理削除の有無"
        LocalDateTime createdAt "作成日時"
        LocalDateTime updatedAt "更新日時"
    }

    ORDER {
        int id "注文ID"
        String OrderDate "注文日時"
        Integer totalAmount "合計金額"
        String customerName "顧客名"
        String customerEmail "顧客のメール"
        String shippingAddress "出荷先住所"
        String phoneNumber "顧客の電話番号"
        String orderStatus "注文完了の有無"
        LocalDateTime createdAt "作成日時"
        LocalDateTime updatedAt "更新日時"
    }

    ORDERITEM {
        int id "注文ID"
        String productName "商品名"
        int unitPrice "販売価格（単価）"
        int salePrice "セール価格（単価）"
        int quantity "数量"
        int subtotal "小計"
        LocalDateTime createdAt "作成日時"
    }

    PRODUCT ||--o{ ORDERITEM : included_in
    ORDER ||--o{ ORDERITEM : contains

</div>
 
 ---

#### エンティティ

| エンティティ名         | 役割               | 説明                                                                 |
|------------------------|--------------------|----------------------------------------------------------------------|
| `PRODUCT`              | 商品情報           | 商品名、価格、セール価格、説明、在庫数、画像、在庫の有無、論理削除の有無、作成日時、更新日時といった商品情報を保持。|
| `ORDER`                | 注文情報           | 注文ID、注文日時、顧客名、顧客のメール、出荷先住所、顧客の電話番号、注文完了の有無、作成日時、更新日時といった情報を保持。 |
| `ORDERITEM`           | 注文の商品明細     | 注文ID、商品名、販売価格（単価）、セール価格（単価）、数量、小計といった情報を保持。        |
---

#### リレーション

- `ORDER` と `ORDERITEM` の関係  
  → 1つの注文は複数の注文商品を含む（1対多）  
  `ORDER ||--o{ ORDERITEM : contains`

- `PRODUCT` と `ORDERITEM` の関係  
  → 1つの商品は複数の注文商品に含まれる（1対多）  
  `PRODUCT ||--o{ ORDERITEM : included_in`