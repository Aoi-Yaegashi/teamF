document.addEventListener('DOMContentLoaded', function() {
    // 追加：全商品リストを保存
    let allProducts = []; 

    // モーダル要素の取得
    const productModal = new bootstrap.Modal(document.getElementById('productModal'));
    const cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
    const checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    const orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const productList = document.getElementById('product-list');

    // APIのベースURL
    const API_BASE = '/api';
    
    // 商品一覧の取得と表示
    if (window.location.pathname !== '/products.html' ) {
        fetchProducts();
    }

    // カート情報の取得と表示
    updateCartDisplay();
    
    // カートボタンクリックイベント
    document.getElementById('cart-btn').addEventListener('click', function() {
        updateCartModalContent();
        cartModal.show();
    });
    
    // 注文手続きボタンクリックイベント
    document.getElementById('checkout-btn').addEventListener('click', function() {
        cartModal.hide();
        checkoutModal.show();
    });
    
    // 注文確定ボタンクリックイベント
    document.getElementById('confirm-order-btn').addEventListener('click', function() {
        submitOrder();
    });
    
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

        // product.htmlの処理
        if (window.location.pathname === '/products.html' ) {
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


    // 商品一覧を取得して表示する関数
    async function fetchProducts() {
        try {
        const response = await fetch(`${API_BASE}/products`);
        if (!response.ok) {
alert('商品の取得に失敗しました');
            throw new Error('商品の取得に失敗しました');

        }
        const products = await response.json();
        allProducts = products;
        displayProducts(products);
    } catch (error) {
        console.error('Error:', error);
        if (window.location.pathname === '/products.html') {
            alert('商品の読み込みに失敗しました');
        }
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
                        <button class="btn btn-outline-primary view-product" data-id="${product.id}">詳細を見る</button>
                    </div>
                </div>
            `;
            container.appendChild(card);
            
            // 詳細ボタンのイベント設定
            card.querySelector('.view-product').addEventListener('click', function() {
                fetchProductDetail(product.id);
            });
        });
    }
    
    // 商品詳細を取得する関数
    async function fetchProductDetail(id) {
        try {
            const response = await fetch(`${API_BASE}/products/${id}`);
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
        
        // カートに追加ボタンのイベント設定
        modalBody.querySelector('.add-to-cart').addEventListener('click', function() {
            const quantity = parseInt(document.getElementById('quantity').value);
            addToCart(product.id, quantity);
        });
        
        productModal.show();
    }
    
    // カートに商品を追加する関数
    async function addToCart(productId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart/add`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: quantity
                })
            });
            
            if (!response.ok) {
                throw new Error('カートへの追加に失敗しました');
            }
            
            const cart = await response.json();
            updateCartBadge(cart.itemCount);
            
            productModal.hide();
            alert('商品をカートに追加しました');
        } catch (error) {
            console.error('Error:', error);
            alert('カートへの追加に失敗しました');
        }
    }
    
    // カート情報を取得する関数
    async function updateCartDisplay() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            updateCartBadge(cart.itemCount);
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    // カートバッジを更新する関数
    function updateCartBadge(count) {
        document.getElementById('cart-count').textContent = count;
    }
    
    // カートモーダルの内容を更新する関数
    async function updateCartModalContent() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            displayCart(cart);
        } catch (error) {
            console.error('Error:', error);
            alert('カート情報の読み込みに失敗しました');
        }
    }
    
    // カート内容を表示する関数
    function displayCart(cart) {
        const modalBody = document.getElementById('cartModalBody');
        
        if (cart.items && Object.keys(cart.items).length > 0) {
            let html = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>商品</th>
                            <th>単価</th>
                            <th>数量</th>
                            <th>小計</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
            `;
            
            Object.values(cart.items).forEach(item => {
                html += `
                    <tr>
                        <td>${item.name}</td>
                        <td>¥${item.price.toLocaleString()}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm update-quantity" 
                                   data-id="${item.id}" value="${item.quantity}" min="1" style="width: 70px">
                        </td>
                        <td>¥${item.subtotal.toLocaleString()}</td>
                        <td>
                            <button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">削除</button>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                    </tbody>
                    <tfoot>
                        <tr>
                            <th colspan="3" class="text-end">合計:</th>
                            <th>¥${cart.totalPrice.toLocaleString()}</th>
                            <th></th>
                        </tr>
                    </tfoot>
                </table>
            `;
            
            modalBody.innerHTML = html;
            
            // 数量更新イベントの設定
            document.querySelectorAll('.update-quantity').forEach(input => {
                input.addEventListener('change', function() {
                    updateItemQuantity(this.dataset.id, this.value);
                });
            });
            
            // 削除ボタンイベントの設定
            document.querySelectorAll('.remove-item').forEach(button => {
                button.addEventListener('click', function() {
                    removeItem(this.dataset.id);
                });
            });
            
            // クリアボタンイベントの設定
            const clearCartBtn = document.getElementById('clearCartBtn');
            if (clearCartBtn) {
                clearCartBtn.addEventListener('click', async function () {
                    if (!confirm('カートをすべて削除しますか？')) return;

                    try {
                        const response = await fetch(`${API_BASE}/cart/clear`, {
                            method: 'DELETE'
                        });

                        if (!response.ok) {
                throw new Error('カートのクリアに失敗しました');
            }

            alert('カートをクリアしました');
            updateCartModalContent(); // カート表示更新
            updateCartBadge(0); // バッジも更新
             } catch (error) {
            console.error(error);
            alert('カートのクリア中にエラーが発生しました');
        }
    });
}

            // 注文ボタンの有効化
            document.getElementById('checkout-btn').disabled = false;
        } else {
            modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
            document.getElementById('checkout-btn').disabled = true;
        }
    }
    
    // カート内の商品数量を更新する関数
    async function updateItemQuantity(productId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: parseInt(quantity)
                })
            });
            
            if (!response.ok) {
                throw new Error('数量の更新に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.itemCount);
        } catch (error) {
            console.error('Error:', error);
            alert('数量の更新に失敗しました');
            updateCartModalContent(); // 失敗時は元の状態に戻す
        }
    }
    
    // カート内の商品を削除する関数
    async function removeItem(productId) {
        try {
            const response = await fetch(`${API_BASE}/cart/remove/${productId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('商品の削除に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.itemCount);
        } catch (error) {
            console.error('Error:', error);
            alert('商品の削除に失敗しました');
        }
    }
    
　　// カートをクリアする関数
async function clearCart() {
    try {
        const response = await fetch(`${API_BASE}/cart/clear`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('カートのクリアに失敗しました');
        alert('カートをクリアしました');
        location.reload();
        const cart = await response.json();
            displayCart(cart);
            updateCartBadge(0);
    } catch (error) {
        console.error(error);
        alert('カートのクリア中にエラーが発生しました');
    }
}


    // 注文を確定する関数
    async function submitOrder() {
        const form = document.getElementById('order-form');
        
        // フォームバリデーション
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        
        const orderData = {
                customerName: document.getElementById('name').value,
                customerEmail: document.getElementById('email').value,
                shippingAddress: document.getElementById('address').value,
                phoneNumber: document.getElementById('phone').value
        };
        
        try {
            const response = await fetch(`${API_BASE}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(orderData)
            });
            
            if (!response.ok) {
                throw new Error('注文の確定に失敗しました');
            }
            
            const order = await response.json();
            displayOrderComplete(order);
            
            checkoutModal.hide();
            orderCompleteModal.show();
            
            // カート表示をリセット
            updateCartBadge(0);
            
            // フォームリセット
            form.reset();
            form.classList.remove('was-validated');
        } catch (error) {
            console.error('Error:', error);
            alert('注文の確定に失敗しました');
        }
    }
    
    // 注文完了画面を表示する関数
    function displayOrderComplete(order) {
        document.getElementById('orderCompleteBody').innerHTML = `
            <p>ご注文ありがとうございます。注文番号は <strong>${order.id}</strong> です。</p>
            <p>ご注文日時: ${new Date(order.orderDate).toLocaleString()}</p>
            <p>お客様のメールアドレスに注文確認メールをお送りしました。</p>
        `;
    }
});