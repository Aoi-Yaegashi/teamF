document.addEventListener('DOMContentLoaded', function() {
  // モーダル要素の取得
    const productModal = new bootstrap.Modal(document.getElementById('productModal'));
  
  //APIのベースURL
    const API_BASE = '/api';

  //商品一覧画面の機能
    //商品一覧の取得と表示
    fetchProducts();

    //編集ボタンクリックイベント

    //商品一覧を取得して表示する関数
    async function fetchProducts() {
      try {
        const response = await fetch(`${API_BASE}/admin`);
        if (!response.ok) {
          throw new Error('商品取得失敗');
        }
        const products = await response.json();
        displayProducts(products);
      } catch (error) {
            console.error('Error:', error);
            alert('商品の読み込みに失敗しました');
        }
    }

  // 商品一覧を表示する関数
  function displayProducts(products) {
    const container = document.getElementById('products-container');
    container.innerHTML = '';
    
    products.forEach(product => {
      const card = document.createElement('div');
      card.className = 'col';
      card.innerHTML = `
        <div class="card product-card">
          <img src="${product.imageUrl || 'https://via.placeholder.com/300x200'}" class="card-img-top" alt="${product.name}">
          <div class="card-body">
            <h5 class="card-title">${product.name}</h5>
            <p class="card-text">¥${product.price.toLocaleString()}</p>
            <button class="btn btn-outline-primary view-product" data-id="${product.id}">編集</button>
          </div>
        </div>
      `;
      container.appendChild(card);
      
      // 編集ボタンのイベント設定
      card.querySelector('.view-product').addEventListener('click', function() {
          fetchProductDetail(product.id);
    });
    });
  }
  // 商品詳細を取得する関数
    async function fetchProductDetail(id) {
        try {
            const response = await fetch(`${API_BASE}/admin/${id}`);
            if (!response.ok) {
                throw new Error('商品詳細の取得に失敗しました');
            }
            const product = await response.json();
            displayProductDetail(product);
        } catch (error) {
            console.error('Error:', error);
            alert('商品詳細の読み込みに失敗しました');
        }
    }

  // 商品詳細を表示する関数
  function displayProductDetail(product) {
      document.getElementById('productModalTitle').textContent = product.name;
      
      const modalBody = document.getElementById('productModalBody');
      modalBody.innerHTML = `
          <div class="row">
              <div class="col-md-6">
                  <img src="${product.imageUrl || 'https://via.placeholder.com/400x300'}" class="img-fluid" alt="${product.name}">
              </div>
              <div class="col-md-6">
                  <p class="fs-4">¥${product.price.toLocaleString()}</p>
                  <p>${product.description}</p>
                  <p>在庫: ${product.stockQuantity} 個</p>
                  <div class="d-flex align-items-center mb-3">
                      <label for="quantity" class="me-2">数量:</label>
                      <input type="number" id="quantity" class="form-control w-25" value="1" min="1" max="${product.stockQuantity}">
                  </div>
                  <button class="btn btn-primary add-to-cart" data-id="${product.id}">カートに入れる</button>
              </div>
          </div>
      `;

      productModal.show();
  }

});