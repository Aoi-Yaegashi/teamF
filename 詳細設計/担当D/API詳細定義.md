
### 1. トップページ表示（カテゴリ一覧取得）

#### `GET /api/top-page`

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "TopPageResponse",
  "type": "object",
  "properties": {
    "categories": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "name":        { "type": "string" }
        },
      }
    },
    "new_arrivals": {
      "type": "array",
      "items": { "$ref": "#/definitions/ProductSummary" }
    },
    "banners": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "image_url": { "type": "string", "format": "uri" },
          "link":     { "type": "string", "format": "uri" }
        },
        "required": ["image_url"]
      }
    }
  },
  "required": ["categories"],
  "definitions": {
    "ProductSummary": {
      "type": "object",
      "properties": {
        "product_id": { "type": "integer" },
        "name":       { "type": "string" },
        "price":      { "type": "number" },
        "image_url":  { "type": "string", "format": "uri" }
      },
      "required": ["product_id","name","price","image_url"]
    }
  }
}
```

---

### 2. 商品一覧ページ表示（商品一覧取得）

#### `GET /api/products`

・リクエスト（クエリパラメータ）



・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title":  "ProductListResponse",
  "type": "object",
  "properties": {
    "products": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "product_id": { "type": "integer" },
          "name":      { "type": "string" },
          "price":     { "type": "number" },
          "image_url":  { "type": "string", "format": "uri" }
        },
        "required": ["product_id","name","price","image_url"]
      }
    }
  },
  "required": ["products"]
}
```

---

### 3. 商品詳細ページ表示（商品詳細取得）

#### `GET /api/products/{product_id}`

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "ProductDetailResponse",
  "type": "object",
  "properties": {
    "product": {
      "type": "object",
      "properties": {
        "product_id":     { "type": "integer" },
        "name":          { "type": "string" },
        "description":   { "type": "string" },
        "price":         { "type": "number" },
        "stock":         { "type": "integer" },
        "image_url":      { "type": "string", "format": "uri" },
        "specifications": {
          "type": "object",
          "properties": {
            "material":   { "type": "string" },
            "dimensions": { "type": "string" }
          }
        }
      },
      "required": ["product_id","name","description","price","stock","image_url"]
    }
  },
  "required": ["product"]
}

```

---

### 4. カートに商品追加

#### `POST /api/cart/add`

・リクエストボディスキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartAddRequest",
  "type": "object",
  "properties": {
    "product_id": { "type": "integer" },
    "quantity":   { "type": "integer", "minimum": 1 }
  },
  "required": ["product_id","quantity"]
}
```

・レスポンス（201 Created）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartAddResponse",
  "type": "object",
  "properties": {
    "cart_items": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "cart_item_id": { "type": "integer" },
          "product_id":   { "type": "integer" },
          "quantity":     { "type": "integer" },
          "name":         { "type": "string" },
          "price":        { "type": "number" }
        },
        "required": ["cart_item_id","product_id","quantity","name","price"]
      }
    }
  },
  "required": ["cart_items"]
}
```

---

### 5. カートページ表示

#### `GET /api/cart`

・リクエスト（クエリパラメータ）

```none
# セッションID は自動的に Cookie 経由で送信
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartGetResponse",
  "type": "object",
  "properties": {
    "cart_id":    { "type": "integer" },
    "cart_items": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "cart_item_id": { "type": "integer" },
          "product_id":   { "type": "integer" },
          "quantity":     { "type": "integer" },
          "name":         { "type": "string" },
          "price":        { "type": "number" },
          "sub_total":    { "type": "number" }
        },
        "required": ["cart_item_id","product_id","quantity","name","price","sub_total"]
      }
    },
    "cart_total":  { "type": "number" }
  },
  "required": ["cart_id","cart_items","cart_total"]
}
```

---

### 6. カート商品数量変更

#### `PUT /api/cart/update`

・リクエストボディスキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartUpdateRequest",
  "type": "object",
  "properties": {
    "cart_item_id": { "type": "integer" },
    "quantity":     { "type": "integer", "minimum": 1 }
  },
  "required": ["cart_item_id","quantity"]
}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartUpdateResponse",
  "type": "object",
  "properties": {
    "updated_cart": { "$ref": "#/definitions/CartGetResponse" }
  },
  "required": ["updated_cart"]
}
```

---

### 7. カート商品削除

#### `DELETE /api/cart`

・リクエストボディスキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartRemoveRequest",
  "type": "object",
  "properties": {
    "cart_item_id": { "type": "integer" }
  },
  "required": ["cart_item_id"]
}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartRemoveResponse",
  "type": "object",
  "properties": {
    "updated_cart": { "$ref": "#/definitions/CartGetResponse" }
  },
  "required": ["updated_cart"]
}
```

---

### 8. 購入手続きページ表示

#### `POST /api/cart/checkout`

・リクエスト（クエリパラメータ）

```none
# セッションID は自動的に Cookie 経由で送信
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CartCheckoutResponse",
  "type": "object",
  "properties": {
    "cart_id": { "type": "integer" },
    "items":   {
      "type": "array",
      "items": { "$ref": "#/definitions/CartItem" }
    },
    "totals": {
      "type": "object",
      "properties": {
        "subtotal": { "type": "number" },
        "tax":      { "type": "number" },
        "shipping": { "type": "number" }
      },
      "required": ["subtotal","tax","shipping"]
    }
  },
  "required": ["cart_id","items","totals"]
}
```

---

### 9. 注文確認画面表示

#### `POST /api/orders/preview`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"OrderPreviewRequest",
  "type":"object",
  "properties":{
    "shipping_address":{ "$ref":"#/definitions/Address" },
    "payment_method":   { "type":"string" },
    "cart_items":       { "type":"array", "items":{ "$ref":"#/definitions/CartItemMinimal" } }
  },
  "required":["shipping_address","payment_method","cart_items"]
}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"OrderPreviewResponse",
  "type":"object",
  "properties":{
    "preview":{
      "type":"object",
      "properties":{
        "subtotal":    { "type":"number" },
        "tax":         { "type":"number" },
        "shipping":    { "type":"number" },
        "discounts":   { "type":"number" },
        "total_amount": { "type":"number" }
      },
      "required":["subtotal","tax","shipping","total_amount"]
    }
  },
  "required":["preview"]
}
```

---

### 10. 注文確定

#### `POST /api/orders`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"OrderCreateRequest",
  "type":"object",
  "properties":{
    "customer_info":    { "$ref":"#/definitions/CustomerInfo" },
    "shipping_address": { "$ref":"#/definitions/Address" },
    "payment_method":   { "type":"string" },
    "cart_items":       { "type":"array", "items":{ "$ref":"#/definitions/CartItemMinimal" } }
  },
  "required":["customer_info","shipping_address","payment_method","cart_items"]
}
```

・レスポンス（201 Created）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"OrderCreateResponse",
  "type":"object",
  "properties":{
    "order_id":      { "type":"integer" },
    "order_number":  { "type":"string" },
    "payment_status":{ "type":"string" }
  },
  "required":["order_id","order_number","payment_status"]
}
```

---

### 11. 注文完了ページ表示

#### `GET /api/orders/{order_id}`

・リクエスト（クエリパラメータ）

```
# パスパラメータ
order_id={integer}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"OrderDetailResponse",
  "type":"object",
  "properties":{
    "order":{ "$ref":"#/definitions/OrderDetail" }
  },
  "required":["order"]
}
```

---

### 12. お問い合わせ送信

#### `POST /api/inquiries`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"InquiryCreateRequest",
  "type":"object",
  "properties":{
    "name":   { "type":"string" },
    "email":  { "type":"string","format":"email" },
    "message":{ "type":"string" }
  },
  "required":["name","email","message"]
}
```

・レスポンス（201 Created）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"InquiryCreateResponse",
  "type":"object",
  "properties":{
    "inquiry_id":{ "type":"integer" }
  },
  "required":["inquiry_id"]
}
```

---

### 13. 管理者ログイン

#### `POST /api/admin/login`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminLoginRequest",
  "type":"object",
  "properties":{
    "username":{ "type":"string" },
    "password":{ "type":"string" }
  },
  "required":["username","password"]
}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminLoginResponse",
  "type":"object",
  "properties":{
    "token":{ "type":"string" },
    "role": { "type":"string" }
  },
  "required":["token","role"]
}
```

---

### 14. ダッシュボード表示

#### `GET /api/admin/dashboard`

・リクエスト（クエリパラメータ）

```none
# Authorization: Bearer <token>
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminDashboardResponse",
  "type":"object",
  "properties":{
    "stats":{
      "type":"object",
      "properties":{
        "total_users": { "type":"integer" },
        "total_orders":{ "type":"integer" },
        "total_revenue":{ "type":"number" },
        "today_orders":{ "type":"integer" }
      },
      "required":["total_users","total_orders","total_revenue"]
    }
  },
  "required":["stats"]
}
```

---

### 15. 商品一覧取得（管理画面）

#### `GET /api/admin/products`

・リクエスト（クエリパラメータ）


・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminProductListResponse",
  "type":"object",
  "properties":{
    "products":{
      "type":"array",
      "items":{ "$ref":"#/definitions/ProductSummary" }
    },
    "pagination":{ "$ref":"#/definitions/Pagination" }
  },
  "required":["products","pagination"]
}
```

---

### 16. 商品登録

#### `POST /api/admin/products`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminProductCreateRequest",
  "type":"object",
  "properties":{
    "name":          { "type":"string" },
    "description":   { "type":"string" },
    "price":         { "type":"number" },
    "image_url":     { "type":"string","format":"uri" },
    "stock_quantity":{ "type":"integer" }
  },
  "required":["name","description","price","image_url","stock_quantity"]
}
```

・レスポンス（201 Created）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminProductCreateResponse",
  "type":"object",
  "properties":{
    "product_id":{ "type":"integer" }
  },
  "required":["product_id"]
}
```

---

### 17. 商品編集

#### `PUT /api/admin/products/{product_id}`

・リクエストボディスキーマ

```json
# AdminProductCreateRequest と同一
```

・レスポンス（204 No Content）

```none
# ボディなし
```

---

### 18. カテゴリ一覧取得（管理画面）

#### `GET /api/admin/categories`

・リクエスト（クエリパラメータ）

```
?page={integer}&limit={integer}
```

・レスポンス（200 OK）スキーマ

```json
# CategoryListResponse + Pagination を組み合わせ
```

---

### 19. カテゴリ登録・編集

#### `POST /api/admin/categories`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminCategoryCreateRequest",
  "type":"object",
  "properties":{
    "name":       { "type":"string" },
    "description":{ "type":"string" }
  },
  "required":["name","description"]
}
```

・レスポンス（201 Created）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminCategoryCreateResponse",
  "type":"object",
  "properties":{
}
```

---

### 20. 注文一覧取得（管理画面）

#### `GET /api/admin/orders`

・リクエスト（クエリパラメータ）

```
?search_keyword={string}&status={string}&page={integer}&limit={integer}
```

・レスポンス（200 OK）スキーマ

```json
# AdminProductListResponse の products→orders、ProductSummary→OrderSummary に置換
```

---

### 21. 注文詳細取得（管理画面）

#### `GET /api/admin/orders/{order_id}`

・リクエスト（クエリパラメータ）

```
# Path: id={integer}
```

・レスポンス（200 OK）スキーマ

```json
# OrderDetailResponse と同一
```

---

### 22. 注文ステータス更新

#### `PUT /api/admin/orders/{order_id}`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminOrderUpdateRequest",
  "type":"object",
  "properties":{
    "status":         { "type":"string" },
    "payment_status": { "type":"string" }
  },
  "required":["status","payment_status"]
}
```

・レスポンス（204 No Content）

```none
# ボディなし
```

---

### 23. 在庫一覧取得（管理画面）

#### `GET /api/admin/inventory`

・リクエスト（クエリパラメータ）

```
?page={integer}&limit={integer}
```

・レスポンス（200 OK）スキーマ

```json
# AdminProductListResponse の products→inventoryItems に置換
```

---

### 24. 在庫一括更新

#### `POST /api/admin/inventory/update`

・リクエストボディスキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminInventoryUpdateRequest",
  "type":"object",
  "properties":{
    "items":{
      "type":"array",
      "items":{
        "type":"object",
        "properties":{
          "product_id":     { "type":"integer" },
          "stock_quantity": { "type":"integer" }
        },
        "required":["product_id","stock_quantity"]
      }
    }
  },
  "required":["items"]
}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminInventoryUpdateResponse",
  "type":"object",
  "properties":{
    "success":{ "type":"boolean" }
  },
  "required":["success"]
}
```

---

### 25. CSVデータダウンロード

#### `GET /api/admin/export/orders`

・リクエスト（クエリパラメータ）

```
?date_range={string}&status={string}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminOrderExportResponse",
  "type":"object",
  "properties":{
    "fileUrl":{ "type":"string","format":"uri" }
  },
  "required":["fileUrl"]
}
```

---

### 26. CSVデータアップロード

#### `POST /api/admin/import/products`

・リクエスト（multipart/form-data）

```
file: CSV ファイル
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema":"http://json-schema.org/draft-07/schema#",
  "title":"AdminProductImportResponse",
  "type":"object",
  "properties":{
    "importedCount":{ "type":"integer" },
    "errors":{ "type":"array","items":{ "type":"string" } }
  },
  "required":["importedCount"]
}
```

---

### 27. お問い合わせ一覧取得（管理画面）

#### `GET /api/admin/inquiries`

・リクエスト（クエリパラメータ）

```
?page={integer}&status={string}
```

・レスポンス（200 OK）スキーマ

```json
# AdminProductListResponse の products→inquiries, ProductSummary→InquirySummary に置換
```

---

### 28. お問い合わせ詳細取得（管理画面）

#### `GET /api/admin/inquiries/{inquiry_id}`

・リクエスト（クエリパラメータ）

```
# Path: id={integer}
```

・レスポンス（200 OK）スキーマ

```json
# OrderDetailResponse の order→inquiry に置換
```

---

### 29. 管理者一覧取得

#### `GET /api/admin/users`

・リクエスト（クエリパラメータ）

```
?page={integer}&limit={integer}
```

・レスポンス（200 OK）スキーマ

```json
# AdminProductListResponse の products→users, ProductSummary→AdminUser に置換
```

---

### 30. 管理者登録・編集

#### `POST /api/admin/users`

・リクエストボディスキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "AdminUserCreateRequest",
  "type": "object",
  "properties": {
    "username": { "type": "string" },
    "password": { "type": "string" },
    "role":     { "type": "string" }
  },
  "required": ["username","password","role"]
}
```

・レスポンス（201 Created）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "AdminUserCreateResponse",
  "type": "object",
  "properties": {
    "admin_user_id": { "type": "integer" }
  },
  "required": ["admin_user_id"]
}
```


### 31. エラーログ一覧取得

#### `GET /api/admin/error-logs`

・リクエスト（クエリパラメータ）

```
?page={integer}&limit={integer}&level={string}&date_range={string}
```

・レスポンス（200 OK）スキーマ

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "AdminErrorLogListResponse",
  "type": "object",
  "properties": {
    "items": {
      "type": "array",
      "items": {
        /* ErrorLog オブジェクト定義 */
      }
    },
    "pagination": {
      "type": "object",
      "properties": {
        "page":       { "type": "integer" },
        "limit":      { "type": "integer" },
        "totalCount": { "type": "integer" },
        "totalPages": { "type": "integer" }
      },
      "required": ["page","limit","totalCount","totalPages"]
    }
  },
  "required": ["items","pagination"]
}
```
