package com.grocery.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FrontendHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String html = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>FreshCart – Online Grocery Delivery</title>
<link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet"/>
<style>
  *,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
  :root{
    --green:#1a6b3c;--green-light:#2d9055;--lime:#b5e030;
    --cream:#faf7f0;--brown:#3b2a1a;--orange:#f06c2a;--white:#ffffff;
    --red:#e53e3e;
  }
  body{font-family:'DM Sans',sans-serif;background:var(--cream);color:var(--brown)}
  nav{position:fixed;top:0;left:0;right:0;z-index:100;display:flex;align-items:center;
      justify-content:space-between;padding:16px 48px;
      background:rgba(250,247,240,0.95);backdrop-filter:blur(12px);
      border-bottom:1px solid rgba(59,42,26,0.08)}
  .logo{font-family:'Playfair Display',serif;font-size:1.5rem;font-weight:900;color:var(--green)}
  .logo span{color:var(--orange)}
  .nav-tabs{display:flex;gap:4px}
  .tab{padding:8px 18px;border-radius:50px;border:none;background:transparent;
       font-family:'DM Sans',sans-serif;font-size:0.88rem;cursor:pointer;
       color:var(--brown);opacity:0.6;transition:all 0.2s;font-weight:500}
  .tab.active{background:var(--green);color:white;opacity:1}
  .tab:hover:not(.active){opacity:1;background:rgba(26,107,60,0.08)}
  .cart-btn{background:var(--green);color:white;padding:9px 20px;border-radius:50px;
            border:none;font-size:0.88rem;font-weight:500;cursor:pointer;transition:all 0.2s}
  .cart-btn:hover{background:var(--green-light)}

  /* PAGES */
  .page{display:none;padding:88px 48px 60px;max-width:1200px;margin:0 auto}
  .page.active{display:block}

  /* HERO */
  .hero-wrap{display:grid;grid-template-columns:1fr 1fr;gap:48px;align-items:center;min-height:80vh}
  .hero-tag{display:inline-block;background:var(--lime);color:var(--green);
            padding:5px 14px;border-radius:50px;font-size:0.75rem;font-weight:500;
            text-transform:uppercase;letter-spacing:0.5px;margin-bottom:16px}
  .hero h1{font-family:'Playfair Display',serif;font-size:clamp(2.4rem,4vw,3.8rem);
           line-height:1.1;font-weight:900;margin-bottom:16px}
  .hero h1 em{color:var(--green);font-style:normal}
  .hero p{font-size:1rem;line-height:1.7;color:rgba(59,42,26,0.65);max-width:420px;margin-bottom:28px}
  .hero-actions{display:flex;gap:12px}
  .btn-primary{background:var(--green);color:white;padding:12px 28px;border-radius:50px;
               border:none;font-size:0.9rem;font-weight:500;cursor:pointer;transition:all 0.2s;
               box-shadow:0 4px 16px rgba(26,107,60,0.3)}
  .btn-primary:hover{background:var(--green-light);transform:translateY(-2px)}
  .hero-visual{background:linear-gradient(135deg,#e8f5e0,#d4edcc);border-radius:32px;
               height:380px;display:flex;align-items:center;justify-content:center;font-size:9rem}

  /* STATS */
  .stats-bar{display:grid;grid-template-columns:repeat(4,1fr);gap:20px;margin-top:40px}
  .stat-card{background:white;border-radius:20px;padding:24px;text-align:center;
             box-shadow:0 2px 12px rgba(0,0,0,0.04)}
  .stat-num{font-family:'Playfair Display',serif;font-size:2rem;font-weight:900;color:var(--green)}
  .stat-label{font-size:0.78rem;color:rgba(59,42,26,0.55);margin-top:4px}

  /* SECTION TITLES */
  .section-label{font-size:0.75rem;text-transform:uppercase;letter-spacing:2px;
                 color:var(--green);font-weight:500;margin-bottom:6px}
  .section-title{font-family:'Playfair Display',serif;font-size:2rem;font-weight:900;margin-bottom:28px}

  /* PRODUCTS */
  .filter-bar{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:28px}
  .filter-btn{padding:7px 18px;border-radius:50px;border:2px solid rgba(59,42,26,0.15);
              background:transparent;font-size:0.82rem;cursor:pointer;transition:all 0.2s;font-family:'DM Sans',sans-serif}
  .filter-btn.active{border-color:var(--green);color:var(--green);font-weight:500}
  .product-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:20px}
  .product-card{background:white;border-radius:20px;overflow:hidden;
                box-shadow:0 2px 12px rgba(0,0,0,0.05);transition:all 0.25s}
  .product-card:hover{transform:translateY(-5px);box-shadow:0 12px 32px rgba(0,0,0,0.1)}
  .product-img{height:160px;background:var(--cream);display:flex;align-items:center;
               justify-content:center;font-size:4.5rem;position:relative}
  .stock-badge{position:absolute;top:10px;right:10px;background:rgba(255,255,255,0.9);
               font-size:0.65rem;padding:3px 8px;border-radius:50px;font-weight:500}
  .low-stock{color:var(--orange)}
  .product-info{padding:14px}
  .product-name{font-weight:500;font-size:0.92rem;margin-bottom:2px}
  .product-unit{font-size:0.75rem;color:rgba(59,42,26,0.5);margin-bottom:10px}
  .product-footer{display:flex;align-items:center;justify-content:space-between}
  .product-price{font-family:'Playfair Display',serif;font-size:1.1rem;font-weight:700;color:var(--green)}
  .add-btn{width:32px;height:32px;border-radius:50%;background:var(--green);color:white;
           border:none;font-size:1.1rem;cursor:pointer;display:flex;align-items:center;
           justify-content:center;transition:all 0.2s}
  .add-btn:hover{background:var(--green-light);transform:scale(1.1)}

  /* CART MODAL */
  .modal-overlay{position:fixed;inset:0;background:rgba(0,0,0,0.4);z-index:200;
                 display:none;align-items:center;justify-content:center}
  .modal-overlay.open{display:flex}
  .modal{background:white;border-radius:28px;padding:36px;width:500px;max-height:80vh;
         overflow-y:auto;position:relative}
  .modal h2{font-family:'Playfair Display',serif;font-size:1.6rem;margin-bottom:20px}
  .modal-close{position:absolute;top:20px;right:20px;background:var(--cream);border:none;
               border-radius:50%;width:36px;height:36px;font-size:1.2rem;cursor:pointer}
  .cart-item{display:flex;align-items:center;gap:12px;padding:12px 0;
             border-bottom:1px solid rgba(59,42,26,0.08)}
  .cart-item-emoji{font-size:2rem;width:50px;text-align:center}
  .cart-item-info{flex:1}
  .cart-item-name{font-weight:500;font-size:0.9rem}
  .cart-item-price{font-size:0.8rem;color:rgba(59,42,26,0.5);margin-top:2px}
  .qty-control{display:flex;align-items:center;gap:8px}
  .qty-btn{width:26px;height:26px;border-radius:50%;border:1px solid rgba(59,42,26,0.2);
           background:white;cursor:pointer;font-size:0.9rem;transition:all 0.15s}
  .qty-btn:hover{background:var(--green);color:white;border-color:var(--green)}
  .cart-total{margin-top:20px;padding:16px;background:var(--cream);border-radius:16px;
              display:flex;justify-content:space-between;align-items:center}
  .cart-total span{font-family:'Playfair Display',serif;font-size:1.2rem;font-weight:700;color:var(--green)}

  /* CHECKOUT FORM */
  .checkout-form{margin-top:20px}
  .form-group{margin-bottom:14px}
  .form-group label{display:block;font-size:0.82rem;font-weight:500;margin-bottom:5px;
                    color:rgba(59,42,26,0.7)}
  .form-group input{width:100%;padding:10px 14px;border:1.5px solid rgba(59,42,26,0.15);
                    border-radius:10px;font-family:'DM Sans',sans-serif;font-size:0.9rem;
                    transition:border-color 0.2s;outline:none}
  .form-group input:focus{border-color:var(--green)}
  .checkout-btn{width:100%;padding:14px;background:var(--green);color:white;border:none;
                border-radius:50px;font-size:0.95rem;font-weight:500;cursor:pointer;
                margin-top:8px;transition:all 0.2s;font-family:'DM Sans',sans-serif}
  .checkout-btn:hover{background:var(--green-light)}

  /* ORDERS PAGE */
  .orders-table{width:100%;border-collapse:collapse;background:white;border-radius:20px;
                overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,0.05)}
  .orders-table th{background:var(--green);color:white;padding:14px 18px;text-align:left;
                   font-size:0.82rem;font-weight:500;text-transform:uppercase;letter-spacing:0.5px}
  .orders-table td{padding:14px 18px;border-bottom:1px solid rgba(59,42,26,0.06);font-size:0.88rem}
  .orders-table tr:last-child td{border-bottom:none}
  .orders-table tr:hover td{background:var(--cream)}
  .status-badge{padding:4px 12px;border-radius:50px;font-size:0.72rem;font-weight:500}
  .status-pending{background:#fff3cd;color:#856404}
  .status-delivered{background:#d1f7e0;color:var(--green)}
  .status-cancelled{background:#f8d7da;color:var(--red)}

  /* CUSTOMERS PAGE */
  .customer-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:20px}
  .customer-card{background:white;border-radius:20px;padding:24px;
                 box-shadow:0 2px 12px rgba(0,0,0,0.05)}
  .customer-avatar{width:48px;height:48px;border-radius:50%;background:var(--green);
                   color:white;display:flex;align-items:center;justify-content:center;
                   font-size:1.2rem;font-weight:700;margin-bottom:12px}
  .customer-name{font-weight:600;font-size:0.95rem;margin-bottom:4px}
  .customer-email{font-size:0.8rem;color:rgba(59,42,26,0.55)}
  .customer-phone{font-size:0.8rem;color:rgba(59,42,26,0.55);margin-top:2px}

  /* ADD CUSTOMER FORM */
  .add-customer-form{background:white;border-radius:24px;padding:32px;
                     box-shadow:0 2px 12px rgba(0,0,0,0.05);margin-bottom:36px}
  .form-row{display:grid;grid-template-columns:1fr 1fr;gap:16px}

  /* TOAST */
  .toast{position:fixed;bottom:32px;right:32px;background:var(--brown);color:white;
         padding:14px 24px;border-radius:14px;font-size:0.88rem;z-index:999;
         transform:translateY(100px);opacity:0;transition:all 0.3s;pointer-events:none}
  .toast.show{transform:translateY(0);opacity:1}

  /* EMPTY STATE */
  .empty{text-align:center;padding:60px 20px;color:rgba(59,42,26,0.4)}
  .empty .icon{font-size:4rem;margin-bottom:12px}

  /* LOADING */
  .loading{text-align:center;padding:40px;color:rgba(59,42,26,0.4);font-size:0.9rem}

  @media(max-width:900px){
    nav{padding:14px 20px}
    .page{padding:80px 20px 40px}
    .hero-wrap{grid-template-columns:1fr}
    .hero-visual{display:none}
    .product-grid{grid-template-columns:repeat(2,1fr)}
    .customer-grid{grid-template-columns:1fr}
    .stats-bar{grid-template-columns:repeat(2,1fr)}
    .form-row{grid-template-columns:1fr}
  }
</style>
</head>
<body>

<nav>
  <div class="logo">Fresh<span>Cart</span></div>
  <div class="nav-tabs">
    <button class="tab active" onclick="showPage('home')">Home</button>
    <button class="tab" onclick="showPage('shop')">Shop</button>
    <button class="tab" onclick="showPage('orders')">Orders</button>
    <button class="tab" onclick="showPage('customers')">Customers</button>
  </div>
  <button class="cart-btn" onclick="openCart()">🛒 Cart (<span id="cartCount">0</span>)</button>
</nav>

<!-- HOME PAGE -->
<div class="page active" id="page-home">
  <div class="hero-wrap">
    <div class="hero">
      <span class="hero-tag">⚡ 30-min delivery</span>
      <h1>Fresh Groceries<br/><em>Delivered Fast</em></h1>
      <p>Shop from thousands of fresh products — fruits, veggies, dairy, and more — delivered straight to your door.</p>
      <div class="hero-actions">
        <button class="btn-primary" onclick="showPage('shop')">Shop Now →</button>
      </div>
    </div>
    <div class="hero-visual">🛒</div>
  </div>
  <div class="stats-bar" id="statsBar">
    <div class="stat-card"><div class="stat-num" id="stat-products">—</div><div class="stat-label">Products</div></div>
    <div class="stat-card"><div class="stat-num" id="stat-customers">—</div><div class="stat-label">Customers</div></div>
    <div class="stat-card"><div class="stat-num" id="stat-orders">—</div><div class="stat-label">Orders</div></div>
    <div class="stat-card"><div class="stat-num" id="stat-revenue">—</div><div class="stat-label">Pending Revenue (₹)</div></div>
  </div>
</div>

<!-- SHOP PAGE -->
<div class="page" id="page-shop">
  <p class="section-label">Browse & Buy</p>
  <h2 class="section-title">Our Products</h2>
  <div class="filter-bar" id="filterBar"></div>
  <div class="product-grid" id="productGrid"><div class="loading">Loading products...</div></div>
</div>

<!-- ORDERS PAGE -->
<div class="page" id="page-orders">
  <p class="section-label">History</p>
  <h2 class="section-title">All Orders</h2>
  <div id="ordersContainer"><div class="loading">Loading orders...</div></div>
</div>

<!-- CUSTOMERS PAGE -->
<div class="page" id="page-customers">
  <p class="section-label">Register</p>
  <h2 class="section-title">Customers</h2>
  <div class="add-customer-form">
    <h3 style="margin-bottom:20px;font-family:'Playfair Display',serif">Add New Customer</h3>
    <div class="form-row">
      <div class="form-group"><label>Full Name *</label><input id="cName" placeholder="e.g. Ravi Kumar"/></div>
      <div class="form-group"><label>Email *</label><input id="cEmail" type="email" placeholder="ravi@email.com"/></div>
    </div>
    <div class="form-row">
      <div class="form-group"><label>Phone</label><input id="cPhone" placeholder="+91 9876543210"/></div>
      <div class="form-group"><label>Address</label><input id="cAddress" placeholder="123, MG Road, Hyderabad"/></div>
    </div>
    <button class="btn-primary" onclick="addCustomer()">+ Register Customer</button>
  </div>
  <div class="customer-grid" id="customerGrid"><div class="loading">Loading customers...</div></div>
</div>

<!-- CART MODAL -->
<div class="modal-overlay" id="cartModal">
  <div class="modal">
    <button class="modal-close" onclick="closeCart()">✕</button>
    <h2>🛒 Your Cart</h2>
    <div id="cartItems"></div>
    <div class="cart-total">
      <span>Total</span>
      <span id="cartTotalAmt">₹0</span>
    </div>
    <div class="checkout-form">
      <h3 style="margin:16px 0 12px;font-family:'Playfair Display',serif;font-size:1.1rem">Checkout</h3>
      <div class="form-group"><label>Your Name *</label><input id="checkoutName" placeholder="Full name"/></div>
      <div class="form-group"><label>Email *</label><input id="checkoutEmail" type="email" placeholder="email@example.com"/></div>
      <button class="checkout-btn" onclick="placeOrder()">Place Order →</button>
    </div>
  </div>
</div>

<div class="toast" id="toast"></div>

<script>
const API = '';
let allProducts = [];
let cart = {};       // { productId: { product, qty } }
let activeFilter = 'All';

// ── NAV ──────────────────────────────────────────────────────────────────────
function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.getElementById('page-' + name).classList.add('active');
  document.querySelectorAll('.tab').forEach(t => {
    if (t.textContent.toLowerCase().includes(name === 'home' ? 'home' :
        name === 'shop' ? 'shop' : name === 'orders' ? 'order' : 'customer'))
      t.classList.add('active');
  });
  if (name === 'shop')      loadProducts();
  if (name === 'orders')    loadOrders();
  if (name === 'customers') loadCustomers();
  if (name === 'home')      loadStats();
}

// ── STATS ─────────────────────────────────────────────────────────────────────
async function loadStats() {
  try {
    const res = await fetch(API + '/api/stats');
    const d   = await res.json();
    document.getElementById('stat-products').textContent  = d.total_products;
    document.getElementById('stat-customers').textContent = d.total_customers;
    document.getElementById('stat-orders').textContent    = d.total_orders;
    document.getElementById('stat-revenue').textContent   = '₹' + d.pending_revenue.toFixed(0);
  } catch(e) { console.error(e); }
}

// ── PRODUCTS ──────────────────────────────────────────────────────────────────
async function loadProducts() {
  if (allProducts.length) { renderProducts(); return; }
  try {
    const res = await fetch(API + '/api/products');
    allProducts = await res.json();
    buildFilters();
    renderProducts();
  } catch(e) {
    document.getElementById('productGrid').innerHTML = '<div class="empty"><div class="icon">⚠️</div>Failed to load products</div>';
  }
}

function buildFilters() {
  const cats = ['All', ...new Set(allProducts.map(p => p.category))];
  const bar  = document.getElementById('filterBar');
  bar.innerHTML = cats.map(c =>
    `<button class="filter-btn ${c==='All'?'active':''}" onclick="filterProducts('${c}')">${c}</button>`
  ).join('');
}

function filterProducts(cat) {
  activeFilter = cat;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.toggle('active', b.textContent === cat));
  renderProducts();
}

function renderProducts() {
  const list   = activeFilter === 'All' ? allProducts : allProducts.filter(p => p.category === activeFilter);
  const grid   = document.getElementById('productGrid');
  if (!list.length) { grid.innerHTML = '<div class="empty"><div class="icon">🔍</div>No products found</div>'; return; }
  grid.innerHTML = list.map(p => `
    <div class="product-card">
      <div class="product-img">${p.emoji}
        <span class="stock-badge ${p.stock < 20 ? 'low-stock' : ''}">
          ${p.stock < 20 ? '⚠️ Low' : '✅ ' + p.stock}
        </span>
      </div>
      <div class="product-info">
        <div class="product-name">${p.name}</div>
        <div class="product-unit">${p.unit}</div>
        <div class="product-footer">
          <span class="product-price">₹${p.price}</span>
          <button class="add-btn" onclick='addToCart(${JSON.stringify(p)})' ${p.stock===0?'disabled':''}>
            ${cart[p.id] ? cart[p.id].qty : '+'}
          </button>
        </div>
      </div>
    </div>`).join('');
}

// ── CART ──────────────────────────────────────────────────────────────────────
function addToCart(product) {
  if (!cart[product.id]) cart[product.id] = { product, qty: 0 };
  cart[product.id].qty++;
  updateCartCount();
  renderProducts();
  showToast(`${product.emoji} ${product.name} added to cart`);
}

function updateCartCount() {
  const total = Object.values(cart).reduce((s,i) => s + i.qty, 0);
  document.getElementById('cartCount').textContent = total;
}

function openCart() {
  renderCartModal();
  document.getElementById('cartModal').classList.add('open');
}

function closeCart() {
  document.getElementById('cartModal').classList.remove('open');
}

function renderCartModal() {
  const items = Object.values(cart);
  const container = document.getElementById('cartItems');
  if (!items.length) {
    container.innerHTML = '<div class="empty"><div class="icon">🛒</div>Your cart is empty</div>';
    document.getElementById('cartTotalAmt').textContent = '₹0';
    return;
  }
  let total = 0;
  container.innerHTML = items.map(({product, qty}) => {
    const sub = product.price * qty;
    total += sub;
    return `<div class="cart-item">
      <div class="cart-item-emoji">${product.emoji}</div>
      <div class="cart-item-info">
        <div class="cart-item-name">${product.name}</div>
        <div class="cart-item-price">₹${product.price} × ${qty} = ₹${sub.toFixed(0)}</div>
      </div>
      <div class="qty-control">
        <button class="qty-btn" onclick="changeQty(${product.id},-1)">−</button>
        <span>${qty}</span>
        <button class="qty-btn" onclick="changeQty(${product.id},1)">+</button>
      </div>
    </div>`;
  }).join('');
  document.getElementById('cartTotalAmt').textContent = '₹' + total.toFixed(0);
}

function changeQty(productId, delta) {
  if (!cart[productId]) return;
  cart[productId].qty += delta;
  if (cart[productId].qty <= 0) delete cart[productId];
  updateCartCount();
  renderCartModal();
  renderProducts();
}

// ── PLACE ORDER ───────────────────────────────────────────────────────────────
async function placeOrder() {
  const name  = document.getElementById('checkoutName').value.trim();
  const email = document.getElementById('checkoutEmail').value.trim();
  if (!name || !email) { showToast('Please enter your name and email'); return; }
  if (!Object.keys(cart).length) { showToast('Cart is empty!'); return; }

  try {
    // Register or get customer
    const custRes  = await fetch(API + '/api/customers', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ name, email })
    });
    const custData = await custRes.json();
    const custId   = custData.id;

    // Place order
    const items = Object.values(cart).map(({product, qty}) => ({
      product_id: product.id, quantity: qty
    }));
    const orderRes  = await fetch(API + '/api/orders', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ customer_id: custId, items })
    });
    const orderData = await orderRes.json();

    if (orderRes.ok) {
      showToast(`✅ Order #${orderData.order_id} placed! Total: ₹${orderData.total.toFixed(0)}`);
      cart = {};
      updateCartCount();
      renderProducts();
      closeCart();
      loadStats();
    } else {
      showToast('❌ ' + orderData.error);
    }
  } catch(e) {
    showToast('❌ Error placing order: ' + e.message);
  }
}

// ── ORDERS ────────────────────────────────────────────────────────────────────
async function loadOrders() {
  try {
    const res    = await fetch(API + '/api/orders');
    const orders = await res.json();
    const cont   = document.getElementById('ordersContainer');
    if (!orders.length) {
      cont.innerHTML = '<div class="empty"><div class="icon">📦</div>No orders yet</div>';
      return;
    }
    cont.innerHTML = `
      <table class="orders-table">
        <thead><tr>
          <th>#</th><th>Customer</th><th>Total</th><th>Status</th><th>Date</th>
        </tr></thead>
        <tbody>${orders.map(o => `
          <tr>
            <td><strong>#${o.id}</strong></td>
            <td>${o.customer}</td>
            <td><strong>₹${o.total.toFixed(0)}</strong></td>
            <td><span class="status-badge status-${o.status}">${o.status}</span></td>
            <td>${new Date(o.created_at).toLocaleString('en-IN')}</td>
          </tr>`).join('')}
        </tbody>
      </table>`;
  } catch(e) {
    document.getElementById('ordersContainer').innerHTML =
      '<div class="empty"><div class="icon">⚠️</div>Failed to load orders</div>';
  }
}

// ── CUSTOMERS ─────────────────────────────────────────────────────────────────
async function loadCustomers() {
  try {
    const res       = await fetch(API + '/api/customers');
    const customers = await res.json();
    const grid      = document.getElementById('customerGrid');
    if (!customers.length) {
      grid.innerHTML = '<div class="empty" style="grid-column:1/-1"><div class="icon">👤</div>No customers yet</div>';
      return;
    }
    grid.innerHTML = customers.map(c => `
      <div class="customer-card">
        <div class="customer-avatar">${c.name.charAt(0).toUpperCase()}</div>
        <div class="customer-name">${c.name}</div>
        <div class="customer-email">✉️ ${c.email}</div>
        ${c.phone ? `<div class="customer-phone">📞 ${c.phone}</div>` : ''}
        ${c.address ? `<div class="customer-phone">📍 ${c.address}</div>` : ''}
      </div>`).join('');
  } catch(e) {
    document.getElementById('customerGrid').innerHTML =
      '<div class="empty"><div class="icon">⚠️</div>Failed to load customers</div>';
  }
}

async function addCustomer() {
  const name    = document.getElementById('cName').value.trim();
  const email   = document.getElementById('cEmail').value.trim();
  const phone   = document.getElementById('cPhone').value.trim();
  const address = document.getElementById('cAddress').value.trim();
  if (!name || !email) { showToast('Name and email are required'); return; }

  try {
    const res  = await fetch(API + '/api/customers', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ name, email, phone, address })
    });
    const data = await res.json();
    if (res.ok) {
      showToast(`✅ Customer "${name}" registered!`);
      document.getElementById('cName').value = '';
      document.getElementById('cEmail').value = '';
      document.getElementById('cPhone').value = '';
      document.getElementById('cAddress').value = '';
      loadCustomers();
    } else {
      showToast('❌ ' + data.error);
    }
  } catch(e) {
    showToast('❌ Error: ' + e.message);
  }
}

// ── TOAST ─────────────────────────────────────────────────────────────────────
function showToast(msg) {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 3000);
}

// ── INIT ──────────────────────────────────────────────────────────────────────
loadStats();
</script>
</body>
</html>
""";
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
