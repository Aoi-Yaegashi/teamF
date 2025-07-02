## 5. データ設計
### 5.1 概念データモデル（ER図）

<div class="mermaid">
erDiagram
    PRODUCT {
        int product_id PK "商品ID"
        string name "商品名"
        string description "商品の詳細説明"
        int category_id FK "紐づくカテゴリID"
        decimal price "販売価格"
        int stock_quantity "在庫数"
        string image_url "商品画像のパスまたはURL"
    }

    CATEGORY {
        int category_id PK "カテゴリID"
        string name "カテゴリ名"
        string description "カテゴリの説明"
    }

    CUSTOMER {
        int customer_id PK "顧客ID"
        string name "氏名"
        string email "メールアドレス"
        string phone_number "電話番号"
        datetime created_at "登録日時"
    }

    ORDER {
        int order_id PK "注文ID"
        int customer_id FK "顧客ID"
        string order_number "注文番号（表示用）"
        datetime order_date "注文日時"
        string status "注文ステータス"
        decimal total_amount "合計金額"
    }

    ORDER_ITEM {
        int order_item_id PK "注文商品ID"
        int order_id FK "注文ID"
        int product_id FK "商品ID"
        int quantity "数量"
        decimal unit_price "単価"
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

    ORDER_STATUS_HISTORY {
        int history_id PK "履歴ID"
        int order_id FK "注文ID"
        string status "注文ステータス"
        datetime changed_at "変更日時"
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

    CATEGORY ||--o{ PRODUCT : includes
    PRODUCT ||--o{ ORDER_ITEM : included_in
    ORDER ||--o{ ORDER_ITEM : contains
    ORDER ||--|| SHIPPING_INFO : has
    ORDER ||--|| PAYMENT_INFO : has
    ORDER ||--o{ ORDER_STATUS_HISTORY : status_tracked_by
    CUSTOMER ||--o{ ORDER : places
    PRODUCT ||--o{ CART_ITEM : added_to
    CART ||--o{ CART_ITEM : contains

</div>
 
 ---

#### エンティティ

| エンティティ名          | 役割                      | 説明                                                                 |
|---------------------------|---------------------------|----------------------------------------------------------------------|
| `CATEGORY`                | 商品カテゴリ              | 商品をカテゴリごとに分類するための情報（名前・説明）を管理します。     |
| `PRODUCT`                 | 商品情報                  | 商品名、価格、説明、画像、在庫数など、ユーザーに表示される商品情報です。 |
| `CUSTOMER`                | 購入者情報                | 注文に紐づく購入者の氏名、連絡先などの基本情報を保持します。          |
| `ORDER`                   | 注文情報                  | 顧客による注文の合計情報（注文番号、合計金額、注文日時など）を保持します。 |
| `ORDER_ITEM`              | 注文の商品明細            | 注文に含まれる商品単位の情報（数量、単価など）を記録します。           |
| `SHIPPING_INFO`           | 配送情報                  | 注文に対しての配送先情報（住所、氏名、電話番号など）を管理します。     |
| `PAYMENT_INFO`            | 支払情報                  | 支払方法、支払状態、支払日時などの決済関連情報を記録します。           |
| `ORDER_STATUS_HISTORY`    | 注文ステータス履歴        | 注文のステータス遷移（例: 受付→出荷）の履歴を記録します。              |
| `CART`                    | カートセッション          | 購入前に商品を一時保存するセッション単位のカート情報です。              |
| `CART_ITEM`               | カート商品明細            | カートに追加された各商品の明細（数量、商品IDなど）を記録します。        |
| `CONTACT`                 | お問い合わせ情報          | 購入者からの問い合わせ内容（氏名、メール、メッセージ等）を保持します。   |
| `PAGE_CONTENT`            | 静的コンテンツページ      | 利用規約、特商法表記、プライバシーポリシーなどの静的ページ情報を管理します。 |
| `ADMIN_USER`              | 管理ユーザー              | 管理者のログイン情報（ユーザー名、パスワードハッシュ、権限）を管理します。 |

#### リレーション一覧

- **CATEGORY** と **PRODUCT** の関係  
  1つのカテゴリ（CATEGORY）は複数の商品（PRODUCT）を含みます（1対多）。  
  ``CATEGORY ||--o{ PRODUCT : includes``

- **CUSTOMER** と **ORDER** の関係  
  1人の顧客（CUSTOMER）は複数の注文（ORDER）を行います（1対多）。  
  ``CUSTOMER ||--o{ ORDER : places``

- **ORDER** と **ORDER_ITEM** の関係  
  1つの注文（ORDER）は複数の注文商品（ORDER_ITEM）を含みます（1対多）。  
  ``ORDER ||--o{ ORDER_ITEM : contains``

- **PRODUCT** と **ORDER_ITEM** の関係  
  1つの商品（PRODUCT）は複数の注文商品（ORDER_ITEM）に含まれます（1対多）。  
  ``PRODUCT ||--o{ ORDER_ITEM : included_in``

- **ORDER** と **SHIPPING_INFO** の関係  
  1つの注文（ORDER）は1つの配送情報（SHIPPING_INFO）を持ちます（1対1）。  
  ``ORDER ||--|| SHIPPING_INFO : has``

- **ORDER** と **PAYMENT_INFO** の関係  
  1つの注文（ORDER）は1つの支払情報（PAYMENT_INFO）を持ちます（1対1）。  
  ``ORDER ||--|| PAYMENT_INFO : has``

- **ORDER** と **ORDER_STATUS_HISTORY** の関係  
  1つの注文（ORDER）は複数の注文ステータス履歴（ORDER_STATUS_HISTORY）を持ちます（1対多）。  
  ``ORDER ||--o{ ORDER_STATUS_HISTORY : status_tracked_by``

- **CART** と **CART_ITEM** の関係  
  1つのカート（CART）は複数のカート商品（CART_ITEM）を含みます（1対多）。  
  ``CART ||--o{ CART_ITEM : contains``

- **PRODUCT** と **CART_ITEM** の関係  
  1つの商品（PRODUCT）は複数のカート商品（CART_ITEM）に含まれます（1対多）。  
  ``PRODUCT ||--o{ CART_ITEM : added_to``