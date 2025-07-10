### PRODUCTSテーブル

#### Create

・商品登録
```sql
INSERT INTO products (
  name, description, material, price, stock_quantity, image_url
) VALUES (
  '花瓶 フラワーベース', 'フラワーベース パステルカラー', NULL, 1, 1980.00, 10, 'https://image.rakuten.co.jp/pease/cabinet/10797533/777-551-22.jpg'
);
```

#### Read

・商品一覧の取得

```sql
SELECT
  product_id,
  name,
  price,
  stock_quantity,
  image_url
FROM
  products
WHERE
  is_deleted = 0
ORDER BY
  name ASC;
```

・商品検索（商品名に「花瓶」を含むもの）
```sql
SELECT *
FROM products
WHERE 
  name LIKE '%花瓶%'
  AND is_deleted = 0;
```

#### Update

・セールの設定変更
```sql
UPDATE products
SET
  stock_quantity = 15,
  sale_price = 1480.00,
  sale_start_date = '2025-08-01 00:00:00',
  sale_end_date = '2025-08-10 23:59:59'
WHERE
  product_id = 1;
```

#### Delete
・`product_id = 1`の商品の削除
```sql
UPDATE products
SET
  is_deleted = 1,
  deleted_at = CURRENT_TIMESTAMP
WHERE product_id = 1;
```

### CUSTOMERSテーブル

#### Create
・顧客登録
```sql
INSERT INTO customers (
  name, email, phone_number, postal_code, address, password_hash, created_at
) VALUES (
  '山田 太郎',
  'taro.yamada@example.com',
  '090-1234-5678',
  '150-0001',
  '東京都渋谷区神宮前1-1-1',
  'abcdefghij',
  CURRENT_TIMESTAMP
);
```

#### Read
・全顧客リストの取得
```sql
SELECT
  customer_id,
  name,
  email,
  phone_number,
  postal_code,
  address,
  created_at
FROM
  customers
ORDER BY
  created_at DESC;
```

#### Update

・パスワードの変更
```sql
UPDATE customers
SET 
  password_hash = 'klmnopqrst'
WHERE 
  customer_id = 1;
```

・住所変更
```sql
UPDATE customers
SET
  postal_code = '160-0022',
  address = '東京都新宿区新宿2-2-2'
WHERE
  customer_id = 1;
```

#### Delete
・`customer_id = 1`の顧客データの削除
```sql
DELETE FROM customers
WHERE customer_id = 1;
```

### ORDERSテーブル

#### Create
・新規注文の登録
```sql
INSERT INTO orders (
  customer_id,
  order_number,
  order_date,
  status,
  total_amount,
  payment_method,
  payment_status,
  payment_date
) VALUES (
  1,
  'ORD-20250704-0001',
  '2025-07-04 10:15:00',
  '支払待ち',
  3960.00,
  'クレジットカード',
  '未',
  NULL
);
```

#### Read
・`customer_id = 1`の注文履歴を取得
```sql
SELECT
  order_id,
  order_number,
  order_date,
  status,
  total_amount,
  payment_status
FROM
  orders
WHERE
  customer_id = 1
ORDER BY
  order_date DESC;
```

#### Update
・注文ステータスの更新
```sql
UPDATE orders
SET
  status = '発送済',
  payment_status = '済',
  payment_date = '2025-07-04 11:45:00'
WHERE
  order_id = 1;
```

#### Delete
```sql
・注文データの削除
DELETE FROM orders
WHERE order_id = 1;
```

### ORDER_ITEMSテーブル

#### Create
・新規注文の登録
```sql
INSERT INTO order_items (
  order_id,
  product_id,
  quantity,
  unit_price
) VALUES
  (1, 1, 2, 1980.00),
  (1, 2, 1, 3000.00);
```

#### Read
・`order_id = 1`の注文明細の取得
```sql
SELECT
  order_items.order_item_id,
  order_items.order_id,
  products.name AS product_name,
  order_items.quantity,
  order_items.unit_price,
  (order_items.quantity * order_items.unit_price) AS subtotal
FROM
  order_items
  INNER JOIN products ON order_items.product_id = products.product_id
WHERE
  order_items.order_id = 1;
```

#### Update
・注文情報の更新（単価と注文数の変更）
```sql
UPDATE order_items
SET
  quantity = 3,
  unit_price = 1800.00
WHERE
  order_item_id = 1;
```

#### Delete
・`order_item_id = 1`の注文明細の削除
```sql
DELETE FROM order_items
WHERE order_item_id = 1;
```