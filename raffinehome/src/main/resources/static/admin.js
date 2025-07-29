document.addEventListener('DOMContentLoaded', function() {
  // 追加：全商品リストを保存
    //let allProducts = []; 
  // モーダル要素の取得
    const productModal = new bootstrap.Modal(document.getElementById('productModal'));
    const createModal = new bootstrap.Modal(document.getElementById('createModal'));

  //APIのベースURL
    const API_BASE = '35.77.82.210:8080/api';

  //商品一覧画面の機能
    //商品一覧の取得と表示
    fetchProducts();

    //編集ボタンクリックイベント
    updateProduct();

   // 商品登録ボタンクリックイベント
    document.getElementById('create-product-btn').addEventListener('click', function() {
        createModal.show();
    });

    // 商品登録確定ボタンクリックイベント
    document.getElementById('create-product').addEventListener('click', function() {
        createProduct();
    });

    // 商品削除ボタンクリックイベント
    deleteProduct();

    //CSVボタンクリックイベント
    document.getElementById('download-btn').addEventListener('click', function(){
        exportProductsForCSV();
    })

    // 検索イベントの設定
            document.getElementById('search-btn').addEventListener('click', function () {
            const keyword = document.getElementById('search-input').value.trim().toLowerCase();
            searchProducts(keyword);
        });

            document.getElementById('search-input').addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
            document.getElementById('search-btn').click();
            }
        });

        // 2%20AdminProductList.htmlの処理
        if (window.location.pathname === '/2%20AdminProductList.html' ) {
            const params = new URLSearchParams(window.location.search);
            const keyword = params.get('keyword');
            
            //このあたり修正
            //fetchProductsしてからsearchする
            fetchProducts().then(() => {
                if (keyword) {
                    searchProducts(keyword);
                }
            });

            // if (keyword) {
            // // 検索処理を呼び出す（例: fetch API などでバックエンド検索）
            //     console.log("検索キーワード:", keyword);
            //     searchProducts(keyword);
            // }
        }else{
            fetchProducts();
        }

    // 検索処理（商品名と説明の両方対象）
    function searchProducts(keyword) {
    if (!keyword) {
            displayProducts(allProducts);
            return;
        }

        const filtered = allProducts.filter(product =>
            product.name.toLowerCase().includes(keyword) ||
            product.description.toLowerCase().includes(keyword)
        );
        displayProducts(filtered);

        if (filtered.length === 0) {
            document.getElementById('products-container').innerHTML =
                '<p class="text-center">検索結果が見つかりませんでした。</p>';
        }
    }

  //商品一覧を取得して表示する関数
  async function fetchProducts() {
    try {
      const response = await fetch(`${API_BASE}/admin`);
      if (!response.ok) {
        throw new Error('商品取得失敗');
      }
      const products = await response.json();
      allProducts = products;
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
            <p class="card-text">通常価格¥${product.price.toLocaleString()}</p>
            <p class="card-text">セール価格¥${product.salePrice.toLocaleString()}</p>
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
                <img src="${product.imageUrl}" class="img-fluid" alt="${product.name}">
            </div>
            <div class="col-md-6">
                <p class="fs-4">通常価格：¥${product.price.toLocaleString()}</p>
                <p class="fs-4">セール価格：¥${product.salePrice.toLocaleString()}</p>
                <p>${product.description}</p>
                <p>在庫: ${product.stockQuantity} 個</p>

        <!-- 商品情報編集フォーム -->
        <form class="form-area" id=update-form>
            <div class="form-group">
              <label for="product-name" class="form-label">新しい商品名</label>
              <input type="text" id="product-name" class="form-input" placeholder="新しい商品名を入力" />
            </div>
            <div class="form-group">
              <label for="product-description" class="form-label">新しい商品説明</label>
              <textarea id="product-description" class="form-textarea" placeholder="新しい商品の説明を入力"></textarea>
            </div>
            <div class="form-group">
              <label for="product-price" class="form-label">新しい販売価格</label>
              <input type="number" id="product-price" class="form-input" placeholder="例: 2980" min="0" />
            </div>
            <div class="form-group">
              <label for="product-saleprice" class="form-label">新しいセール価格</label>
              <input type="number" id="product-saleprice" class="form-input" placeholder="例: 1980" min="0" />
            </div>
            <div class="form-group">
              <label for="product-stockquantity" class="form-label">新しい在庫量</label>
              <input type="number" id="product-stockquantity" class="form-input" placeholder="例: 10" min="0" />
            </div>
            <div class="form-group">
                <label for="product-image" class="form-label">商品の新しい画像パス</label>
                <input type="text" id="product-image" class="form-input" placeholder="新しい画像のパスを入力" />
            </div>
        </form>
                <button class="btn btn-primary update-product" id=update-product data-id="${product.id}">確定</button>
                <button class="btn btn-primary delete-product" id=delete-product data-id="${product.id}">削除</button>
            </div>
        </div>
    `;

    //フォームの初期値として、既存情報が入力されているようにしたい
    document.getElementById('product-name').value = product.name || '';
    document.getElementById('product-description').value = product.description || '';
    document.getElementById('product-price').value = product.price || '';
    document.getElementById('product-saleprice').value = product.salePrice || '';
    document.getElementById('product-stockquantity').value = product.stockQuantity || '';
    document.getElementById('product-image').value = product.imageUrl || '';

      // 更新確定ボタンのイベント設定
      modalBody.querySelector('.update-product').addEventListener('click', function() {
          /*const quantity = parseInt(document.getElementById('quantity').value);*/
          updateProduct(product.id);
      });

      //削除ボタンのイベント設定
      modalBody.querySelector('.delete-product').addEventListener('click', function() {
          deleteProduct(product.id);
      });

    productModal.show();
  }

  // 商品更新を確定する関数
  async function updateProduct(id) {
    const form = document.getElementById('update-form');

    // フォームバリデーション
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    const updateData = {
      //formInfo: {
        name: document.getElementById('product-name').value,
        description: document.getElementById('product-description').value,
        price: document.getElementById('product-price').value,
        salePrice: document.getElementById('product-saleprice').value,
        stockQuantity: document.getElementById('product-stockquantity').value,
        imageUrl: document.getElementById('product-image').value
      //}
    };

    try {
        const response = await fetch(`${API_BASE}/admin/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData)
        });

        if (!response.ok) {
            throw new Error('更新に失敗しました');
        }

        try{

          productModal.hide();
          alert('更新が完了しました');

        } catch (error) {
            console.error('Error:', error);
            alert('更新に失敗しました');
        }

    form.reset();
    form.classList.remove('was-validated');
    } catch (error) {
            console.error('Error:', error);
            alert('更新に失敗しました');
      }   
  }

  // 商品を論理削除する関数
  async function deleteProduct(id) {

    const deleteData = {
      isDeleted: true.valueOf
    }

    try {
        const response = await fetch(`${API_BASE}/admin/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(deleteData)
        });

        if (!response.ok) {
            throw new Error('削除に失敗しました');
        }

        //const deleteflag = await response.json();
        //productModal.isdeleted = deleteflag;

        try{

          productModal.hide();
          alert('削除が完了しました');

        } catch (error) {
            console.error('Error:', error);
            
            alert('削除に失敗しました');
        }

    } catch (error) {
            console.error('Error:', error);
            if (window.location.pathname === '/products.html') {
            alert('削除に失敗しました');
            }
      }   
  }

  //商品の追加を確定する関数
  async function createProduct() {
      const form = document.getElementById('create-form');

      // フォームバリデーション
      if (!form.checkValidity()) {
          form.classList.add('was-validated');
          return;
      }

      const createData = {
        //formInfo: {
          name: document.getElementById('product-name').value,
          description: document.getElementById('product-description').value,
          price: document.getElementById('product-price').value,
          salePrice: document.getElementById('product-saleprice').value,
          stockQuantity: document.getElementById('product-stockquantity').value,
          imageUrl: document.getElementById('product-image').value
        //}
      };

      try {
          const response = await fetch(`${API_BASE}/admin`, {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify(createData)
          });

          if (!response.ok) {
              throw new Error('登録に失敗しました');
          }

          try{

            createModal.hide();
            alert('登録が完了しました');

          } catch (error) {
              console.error('Error:', error);
              alert('登録に失敗しました');
          }

      form.reset();
      form.classList.remove('was-validated');
        } catch (error) {
                console.error('Error:', error);
                alert('登録に失敗しました');
            }
  }
  
    //CSVファイルにエクスポートする関数
    function exportProductsForCSV(){
        fetch('/api/admin/export')
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'products.csv';
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(error => console.error('CSVダウンロード失敗:', error));
    }
});