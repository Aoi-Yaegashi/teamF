## 5. データ設計
### 5.1 概念データモデル（ER図）

<div class="mermaid">
erDiagram
    PRODUCT {
        int id PK "商品ID"
        string name "商品名"
        string description "商品の詳細説明"
        int price "販売価格"
        int salePrice "セール価格"
        int stockQuantity "在庫数"
        string image_url "商品画像のパスまたはURL"
        boolean isInStock "在庫の有無"
        boolean isDeleted "論理削除の有無"
    }

    ORDER {
            int id "注文ID"
            String customerName "顧客名"
            String customerEmail "顧客のeメール"
            String shippingAddress "出荷先住所"
            String postalCode "郵便番号"
            String phoneNumber "顧客の電話番号"
            int subtotal "小計"
            int totalAmount "合計"
            String orderStatus "注文完了の有無"
            LocalDateTime orderDate "注文日"
            Boolean canCancel "キャンセルの有無"
            Boolean canShip "出荷完了の有無"
    }

    ORDER_ITEM {
        int order_item_id PK "注文商品ID"
        int order_id FK "注文ID"
        int product_id FK "商品ID"
        int quantity "数量"
        decimal unit_price "単価"
    }

    ORDER_CREATE{
      String customerName;
    
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String customerEmail;
    
    @NotBlank(message = "住所は必須です")
    private String shippingAddress;
    
    @NotBlank(message = "電話番号は必須です")
    private String phoneNumber

    }

    SHIPPING_INFO {
        int shipping_info_id PK "配送情報ID"
        int order_id FK "注文ID"
        string recipient_name "宛先氏名"
        string postal_code "郵便番号"
        string address "住所"
        string phone_number "電話番号"
    }

    PAYMENT_INFO {
        int payment_info_id PK "支払情報ID"
        int order_id FK "注文ID"
        string payment_method "支払い方法"
        string payment_status "支払いステータス"
        datetime payment_date "支払い日時"
    }


    CART {
        int cart_id PK "カートID"
        string session_token "セッショントークン"
        datetime created_at "作成日時"
        datetime updated_at "更新日時"
    }

    CART_ITEM {
        int cart_item_id PK "カート商品ID"
        int cart_id FK "カートID"
        int product_id FK "商品ID"
        int quantity "数量"
        datetime added_at "追加日時"
    }

    CONTACT {
        int contact_id PK "問い合わせID"
        string name "名前"
        string email "メールアドレス"
        string message "お問い合わせ内容"
        datetime created_at "送信日時"
    }

    PAGE_CONTENT {
        int page_id PK "ページID"
        string title "ページタイトル"
        text content "本文"
        datetime updated_at "更新日時"
    }

    ADMIN_USER {
        int admin_user_id PK "管理ユーザーID"
        string username "ユーザー名"
        string password_hash "パスワードハッシュ"
        string role "権限区分"
    }

    SALE_PRICE {
    int sale_price_id PK "セール価格ID"
    int product_id FK "対象商品ID"
    decimal discounted_price "割引価格"
    datetime start_date "セール開始日"
    datetime end_date "セール終了日"
    }

    CATEGORY ||--o{ PRODUCT : includes
    PRODUCT ||--o{ ORDER_ITEM : included_in
    ORDER ||--o{ ORDER_ITEM : contains
    ORDER_ITEM ||--o{ ORDER_CREATE : contains
    ORDER ||--|| SHIPPING_INFO : has
    ORDER ||--|| PAYMENT_INFO : has
    CUSTOMER ||--o{ ORDER : places
    PRODUCT ||--o{ CART_ITEM : added_to
    CART ||--o{ CART_ITEM : contains
    PRODUCT ||--o{ SALE_PRICE : has_discounted_price

</div>
 
 ---

#### エンティティ

| エンティティ名         | 役割               | 説明                                                                 |
|------------------------|--------------------|----------------------------------------------------------------------|
| `CATEGORY`             | 商品カテゴリ       | 商品をカテゴリごとに分類するための情報（名前・説明）を管理。         |
| `PRODUCT`              | 商品情報           | 商品名、価格、説明、画像、在庫数など、ユーザーに表示される商品情報。 |
| `SALE_PRICE`           | セール価格         | 商品ごとに設定可能なセール価格（割引価格、開始日時、終了日時など）を管理。 |
| `CUSTOMER`             | 購入者情報         | 注文に紐づく購入者の氏名、連絡先などの基本情報を保持。              |
| `ORDER`                | 注文情報           | 顧客による注文の合計情報（注文番号、合計金額、注文日時など）を保持。 |
| `ORDER_ITEM`           | 注文の商品明細     | 注文に含まれる商品単位の情報（数量、単価など）を記録。               |
| `SHIPPING_INFO`        | 配送情報           | 注文に対しての配送先情報（住所、氏名、電話番号など）を管理。         |
| `PAYMENT_INFO`         | 支払情報           | 支払方法、支払状態、支払日時などの決済関連情報を記録。               |
| `ORDER_STATUS_HISTORY` | 注文ステータス履歴 | 注文のステータス遷移（例: 受付→出荷）の履歴を記録。                  |
| `CART`                 | カートセッション   | 購入前に商品を一時保存するセッション単位のカート情報。                |
| `CART_ITEM`            | カート商品明細     | カートに追加された各商品の明細（数量、商品IDなど）を記録。            |
| `CONTACT`              | お問い合わせ情報   | 購入者からの問い合わせ内容（氏名、メール、メッセージ等）を保持。     |
| `PAGE_CONTENT`         | 静的コンテンツページ | 利用規約、特商法表記、プライバシーポリシーなどの静的ページ情報を管理。 |
| `ADMIN_USER`           | 管理ユーザー       | 管理者のログイン情報（ユーザー名、パスワードハッシュ、権限）を管理。 |

---

#### リレーション

- `CATEGORY` と `PRODUCT` の関係  
  → 1つのカテゴリは複数の商品を含む（1対多）  
  `CATEGORY ||--o{ PRODUCT : includes`

- `PRODUCT` と `SALE_PRICE` の関係  
  → 1つの商品には複数のセール価格設定が可能（1対多）  
  `PRODUCT ||--o{ SALE_PRICE : has_discounted_price`

- `CUSTOMER` と `ORDER` の関係  
  → 1人の顧客は複数の注文を行う（1対多）  
  `CUSTOMER ||--o{ ORDER : places`

- `ORDER` と `ORDER_ITEM` の関係  
  → 1つの注文は複数の注文商品を含む（1対多）  
  `ORDER ||--o{ ORDER_ITEM : contains`

- `PRODUCT` と `ORDER_ITEM` の関係  
  → 1つの商品は複数の注文商品に含まれる（1対多）  
  `PRODUCT ||--o{ ORDER_ITEM : included_in`

- `ORDER` と `SHIPPING_INFO` の関係  
  → 1つの注文は1つの配送情報を持つ（1対1）  
  `ORDER ||--|| SHIPPING_INFO : has`

- `ORDER` と `PAYMENT_INFO` の関係  
  → 1つの注文は1つの支払情報を持つ（1対1）  
  `ORDER ||--|| PAYMENT_INFO : has`

- `ORDER` と `ORDER_STATUS_HISTORY` の関係  
  → 1つの注文は複数の注文ステータス履歴を持つ（1対多）  
  `ORDER ||--o{ ORDER_STATUS_HISTORY : status_tracked_by`

- `CART` と `CART_ITEM` の関係  
  → 1つのカートは複数のカート商品を含む（1対多）  
  `CART ||--o{ CART_ITEM : contains`

- `PRODUCT` と `CART_ITEM` の関係  
  → 1つの商品は複数のカート商品に含まれる（1対多）  
  `PRODUCT ||--o{ CART_ITEM : added_to`
