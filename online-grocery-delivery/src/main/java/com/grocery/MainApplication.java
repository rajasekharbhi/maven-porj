package com.grocery;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MainApplication {

    public String getWelcomeMessage() {
        return "Welcome to Online Grocery Delivery!";
    }

    static class RootHandler implements HttpHandler {
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
  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

  :root {
    --green: #1a6b3c;
    --green-light: #2d9055;
    --lime: #b5e030;
    --cream: #faf7f0;
    --brown: #3b2a1a;
    --orange: #f06c2a;
    --white: #ffffff;
  }

  body {
    font-family: 'DM Sans', sans-serif;
    background: var(--cream);
    color: var(--brown);
    overflow-x: hidden;
  }

  /* NAV */
  nav {
    position: fixed; top: 0; left: 0; right: 0; z-index: 100;
    display: flex; align-items: center; justify-content: space-between;
    padding: 18px 48px;
    background: rgba(250,247,240,0.92);
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(59,42,26,0.08);
  }
  .logo {
    font-family: 'Playfair Display', serif;
    font-size: 1.6rem; font-weight: 900;
    color: var(--green);
    letter-spacing: -0.5px;
  }
  .logo span { color: var(--orange); }
  .nav-links { display: flex; gap: 32px; list-style: none; }
  .nav-links a { text-decoration: none; color: var(--brown); font-size: 0.9rem; font-weight: 500; opacity: 0.75; transition: opacity 0.2s; }
  .nav-links a:hover { opacity: 1; }
  .nav-cta {
    background: var(--green); color: white;
    padding: 10px 24px; border-radius: 50px;
    font-size: 0.88rem; font-weight: 500;
    cursor: pointer; border: none;
    transition: background 0.2s, transform 0.15s;
  }
  .nav-cta:hover { background: var(--green-light); transform: translateY(-1px); }

  /* HERO */
  .hero {
    min-height: 100vh;
    display: grid; grid-template-columns: 1fr 1fr;
    align-items: center;
    padding: 100px 48px 60px;
    gap: 48px;
    position: relative;
    overflow: hidden;
  }
  .hero::before {
    content: '';
    position: absolute; top: -100px; right: -100px;
    width: 600px; height: 600px;
    background: radial-gradient(circle, rgba(181,224,48,0.18) 0%, transparent 70%);
    border-radius: 50%;
    animation: pulse 6s ease-in-out infinite;
  }
  @keyframes pulse { 0%,100% { transform: scale(1); } 50% { transform: scale(1.08); } }

  .hero-tag {
    display: inline-block;
    background: var(--lime); color: var(--green);
    padding: 6px 16px; border-radius: 50px;
    font-size: 0.78rem; font-weight: 500;
    letter-spacing: 0.5px; text-transform: uppercase;
    margin-bottom: 20px;
    animation: fadeUp 0.6s ease both;
  }
  .hero h1 {
    font-family: 'Playfair Display', serif;
    font-size: clamp(2.8rem, 5vw, 4.2rem);
    line-height: 1.1; font-weight: 900;
    color: var(--brown);
    margin-bottom: 20px;
    animation: fadeUp 0.6s 0.1s ease both;
  }
  .hero h1 em { color: var(--green); font-style: normal; }
  .hero p {
    font-size: 1.05rem; line-height: 1.7;
    color: rgba(59,42,26,0.65);
    max-width: 440px; margin-bottom: 36px;
    animation: fadeUp 0.6s 0.2s ease both;
  }
  .hero-actions { display: flex; gap: 16px; flex-wrap: wrap; animation: fadeUp 0.6s 0.3s ease both; }
  .btn-primary {
    background: var(--green); color: white;
    padding: 14px 32px; border-radius: 50px;
    font-size: 0.95rem; font-weight: 500;
    cursor: pointer; border: none;
    transition: all 0.2s; box-shadow: 0 4px 20px rgba(26,107,60,0.3);
  }
  .btn-primary:hover { background: var(--green-light); transform: translateY(-2px); box-shadow: 0 8px 28px rgba(26,107,60,0.4); }
  .btn-secondary {
    background: transparent; color: var(--brown);
    padding: 14px 32px; border-radius: 50px;
    font-size: 0.95rem; font-weight: 500;
    cursor: pointer; border: 2px solid rgba(59,42,26,0.2);
    transition: all 0.2s;
  }
  .btn-secondary:hover { border-color: var(--green); color: var(--green); }

  .hero-stats {
    display: flex; gap: 36px; margin-top: 48px;
    animation: fadeUp 0.6s 0.4s ease both;
  }
  .stat-num { font-family: 'Playfair Display', serif; font-size: 2rem; font-weight: 900; color: var(--green); }
  .stat-label { font-size: 0.78rem; color: rgba(59,42,26,0.55); margin-top: 2px; }

  /* HERO VISUAL */
  .hero-visual {
    position: relative; display: flex; justify-content: center; align-items: center;
    animation: fadeIn 0.8s 0.2s ease both;
  }
  .hero-circle {
    width: 440px; height: 440px; border-radius: 50%;
    background: linear-gradient(135deg, #e8f5e0 0%, #d4edcc 100%);
    display: flex; align-items: center; justify-content: center;
    font-size: 10rem;
    box-shadow: 0 24px 80px rgba(26,107,60,0.15);
    position: relative;
  }
  .floating-badge {
    position: absolute;
    background: white; border-radius: 16px;
    padding: 12px 18px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    display: flex; align-items: center; gap: 10px;
    font-size: 0.82rem; font-weight: 500;
  }
  .badge-1 { top: 20px; right: -20px; animation: float 4s ease-in-out infinite; }
  .badge-2 { bottom: 40px; left: -30px; animation: float 4s 2s ease-in-out infinite; }
  .badge-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--lime); }
  @keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }

  /* CATEGORIES */
  .categories {
    padding: 80px 48px;
    background: white;
  }
  .section-label {
    font-size: 0.78rem; text-transform: uppercase; letter-spacing: 2px;
    color: var(--green); font-weight: 500; margin-bottom: 8px;
  }
  .section-title {
    font-family: 'Playfair Display', serif;
    font-size: 2.2rem; font-weight: 900; margin-bottom: 40px;
  }
  .category-grid {
    display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px;
  }
  .category-card {
    background: var(--cream); border-radius: 20px;
    padding: 24px 16px; text-align: center;
    cursor: pointer; transition: all 0.25s;
    border: 2px solid transparent;
  }
  .category-card:hover { border-color: var(--lime); transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.08); }
  .category-card .emoji { font-size: 2.4rem; margin-bottom: 10px; display: block; }
  .category-card .name { font-size: 0.82rem; font-weight: 500; color: var(--brown); }

  /* PRODUCTS */
  .products { padding: 80px 48px; }
  .section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 36px; }
  .view-all { color: var(--green); font-size: 0.88rem; font-weight: 500; cursor: pointer; text-decoration: underline; }
  .product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
  .product-card {
    background: white; border-radius: 24px;
    overflow: hidden; transition: all 0.25s;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  }
  .product-card:hover { transform: translateY(-6px); box-shadow: 0 16px 40px rgba(0,0,0,0.1); }
  .product-img {
    height: 180px; background: var(--cream);
    display: flex; align-items: center; justify-content: center;
    font-size: 5rem; position: relative;
  }
  .product-badge {
    position: absolute; top: 12px; left: 12px;
    background: var(--orange); color: white;
    padding: 3px 10px; border-radius: 50px;
    font-size: 0.7rem; font-weight: 500;
  }
  .product-info { padding: 18px; }
  .product-name { font-weight: 500; margin-bottom: 4px; font-size: 0.95rem; }
  .product-weight { font-size: 0.78rem; color: rgba(59,42,26,0.5); margin-bottom: 12px; }
  .product-footer { display: flex; align-items: center; justify-content: space-between; }
  .product-price { font-family: 'Playfair Display', serif; font-size: 1.2rem; font-weight: 700; color: var(--green); }
  .add-btn {
    width: 34px; height: 34px; border-radius: 50%;
    background: var(--green); color: white; border: none;
    font-size: 1.2rem; cursor: pointer;
    display: flex; align-items: center; justify-content: center;
    transition: all 0.2s;
  }
  .add-btn:hover { background: var(--green-light); transform: scale(1.1); }

  /* BANNER */
  .banner {
    margin: 0 48px 80px;
    background: linear-gradient(135deg, var(--green) 0%, var(--green-light) 100%);
    border-radius: 32px; padding: 60px 64px;
    display: flex; align-items: center; justify-content: space-between;
    overflow: hidden; position: relative;
  }
  .banner::before {
    content: '🌿';
    position: absolute; right: 200px; top: -20px;
    font-size: 8rem; opacity: 0.12;
  }
  .banner-text h2 {
    font-family: 'Playfair Display', serif;
    font-size: 2.2rem; color: white; font-weight: 900; margin-bottom: 12px;
  }
  .banner-text p { color: rgba(255,255,255,0.75); font-size: 1rem; max-width: 380px; }
  .banner-emoji { font-size: 7rem; }

  /* FOOTER */
  footer {
    background: var(--brown); color: rgba(255,255,255,0.7);
    padding: 48px; text-align: center; font-size: 0.85rem;
  }
  footer .footer-logo {
    font-family: 'Playfair Display', serif;
    font-size: 1.8rem; color: white; font-weight: 900; margin-bottom: 12px;
  }
  footer .footer-logo span { color: var(--lime); }

  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(24px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  @keyframes fadeIn {
    from { opacity: 0; } to { opacity: 1; }
  }

  @media (max-width: 900px) {
    .hero { grid-template-columns: 1fr; padding: 100px 24px 60px; }
    .hero-visual { display: none; }
    .category-grid { grid-template-columns: repeat(3, 1fr); }
    .product-grid { grid-template-columns: repeat(2, 1fr); }
    nav { padding: 16px 24px; }
    .nav-links { display: none; }
  }
</style>
</head>
<body>

<nav>
  <div class="logo">Fresh<span>Cart</span></div>
  <ul class="nav-links">
    <li><a href="#">Home</a></li>
    <li><a href="#">Shop</a></li>
    <li><a href="#">Offers</a></li>
    <li><a href="#">About</a></li>
  </ul>
  <button class="nav-cta">🛒 Cart (0)</button>
</nav>

<section class="hero">
  <div class="hero-content">
    <span class="hero-tag">⚡ 30-min delivery</span>
    <h1>Fresh Groceries<br/><em>Delivered Fast</em></h1>
    <p>Shop from thousands of fresh products — fruits, veggies, dairy, and more — delivered straight to your door in under 30 minutes.</p>
    <div class="hero-actions">
      <button class="btn-primary">Shop Now →</button>
      <button class="btn-secondary">View Offers</button>
    </div>
    <div class="hero-stats">
      <div><div class="stat-num">5K+</div><div class="stat-label">Products</div></div>
      <div><div class="stat-num">30m</div><div class="stat-label">Avg. Delivery</div></div>
      <div><div class="stat-num">98%</div><div class="stat-label">Happy Customers</div></div>
    </div>
  </div>

  <div class="hero-visual">
    <div class="hero-circle">🛒
      <div class="floating-badge badge-1"><div class="badge-dot"></div> Just delivered!</div>
      <div class="floating-badge badge-2">🥦 Fresh today</div>
    </div>
  </div>
</section>

<section class="categories">
  <p class="section-label">Browse by</p>
  <h2 class="section-title">Categories</h2>
  <div class="category-grid">
    <div class="category-card"><span class="emoji">🥦</span><div class="name">Vegetables</div></div>
    <div class="category-card"><span class="emoji">🍎</span><div class="name">Fruits</div></div>
    <div class="category-card"><span class="emoji">🥛</span><div class="name">Dairy</div></div>
    <div class="category-card"><span class="emoji">🍞</span><div class="name">Bakery</div></div>
    <div class="category-card"><span class="emoji">🥩</span><div class="name">Meat</div></div>
    <div class="category-card"><span class="emoji">🧃</span><div class="name">Beverages</div></div>
  </div>
</section>

<section class="products">
  <div class="section-header">
    <div>
      <p class="section-label">Today's picks</p>
      <h2 class="section-title">Featured Products</h2>
    </div>
    <span class="view-all">View all →</span>
  </div>
  <div class="product-grid">
    <div class="product-card">
      <div class="product-img">🍅<span class="product-badge">Fresh</span></div>
      <div class="product-info">
        <div class="product-name">Organic Tomatoes</div>
        <div class="product-weight">500g pack</div>
        <div class="product-footer"><span class="product-price">₹49</span><button class="add-btn">+</button></div>
      </div>
    </div>
    <div class="product-card">
      <div class="product-img">🥑<span class="product-badge">Sale</span></div>
      <div class="product-info">
        <div class="product-name">Avocados</div>
        <div class="product-weight">2 pieces</div>
        <div class="product-footer"><span class="product-price">₹89</span><button class="add-btn">+</button></div>
      </div>
    </div>
    <div class="product-card">
      <div class="product-img">🥛</div>
      <div class="product-info">
        <div class="product-name">Full Cream Milk</div>
        <div class="product-weight">1 litre</div>
        <div class="product-footer"><span class="product-price">₹62</span><button class="add-btn">+</button></div>
      </div>
    </div>
    <div class="product-card">
      <div class="product-img">🍌<span class="product-badge">Popular</span></div>
      <div class="product-info">
        <div class="product-name">Bananas</div>
        <div class="product-weight">6 pieces</div>
        <div class="product-footer"><span class="product-price">₹35</span><button class="add-btn">+</button></div>
      </div>
    </div>
  </div>
</section>

<div class="banner">
  <div class="banner-text">
    <h2>Get 20% off your first order!</h2>
    <p>Use code <strong style="color:var(--lime)">FRESH20</strong> at checkout. Valid on orders above ₹299.</p>
  </div>
  <div class="banner-emoji">🎉</div>
</div>

<footer>
  <div class="footer-logo">Fresh<span>Cart</span></div>
  <p>© 2025 FreshCart – Online Grocery Delivery. All rights reserved.</p>
</footer>

<script>
  // Add to cart interaction
  let count = 0;
  document.querySelectorAll('.add-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      count++;
      document.querySelector('.nav-cta').textContent = '🛒 Cart (' + count + ')';
      btn.textContent = '✓';
      btn.style.background = '#b5e030';
      btn.style.color = '#1a6b3c';
      setTimeout(() => { btn.textContent = '+'; btn.style.background = ''; btn.style.color = ''; }, 1000);
    });
  });
</script>
</body>
</html>
""";
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("FreshCart running at http://localhost:" + port);
    }
}
