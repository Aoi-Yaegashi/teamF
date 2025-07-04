### PRODUCTSテーブル

#### Create

・商品登録
```sql
INSERT INTO products (
  name, description, material, category_id, price, stock_quantity, image_url
) VALUES (
  '花瓶 フラワーベース', 'フラワーベース パステルカラー', NULL, 1, 1980.00, 10, 'https://image.rakuten.co.jp/pease/cabinet/10797533/777-551-22.jpg'
);
```

#### Read

・商品一覧の取得（在庫があるもの）

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
  stock_quantity > 0
ORDER BY
  name ASC;
```

・商品検索（商品名に「花瓶」を含むもの）
```sql
SELECT *
FROM products
WHERE name LIKE '%花瓶%';
```

#### Update

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


### CUSTOMERSテーブル

#### Create

#### Read

#### Update

#### Delete

### ORDERSテーブル

#### Create

#### Read

#### Update

#### Delete