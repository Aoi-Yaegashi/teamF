document.addEventListener('DOMContentLoaded', () => {
  const container = document.querySelector('.products-list');

  async function fetchProducts() {
    try {
      const res = await fetch('/api/products');
      if (!res.ok) throw new Error('商品取得失敗');
      const products = await res.json();
      renderProducts(products);
    } catch (e) {
      alert(e.message);
    }
  }

  function renderProducts(products) {
    container.innerHTML = '';
    products.forEach(p => {
      const div = document.createElement('div');
      div.className = 'product-card';
      div.innerHTML = `
        <label class="product-photo-wrapper" tabindex="0" aria-label="商品写真を変更">
          <img src="${p.imageUrl || '/images/no-image.png'}" alt="商品の写真" class="product-photo" />
          <input type="file" accept="image/*" class="product-photo-input" />
        </label>
        <div class="product-info">
          <div class="product-title">${p.name}</div>
          <div class="product-price">¥${p.price.toLocaleString()}</div>
          <div class="product-desc">${p.description}</div>
          <div class="product-stock">在庫数: ${p.stockQuantity}</div>
        </div>
        <button class="edit-btn">編集</button>
      `;
      container.appendChild(div);
    });
  }

  fetchProducts();
});