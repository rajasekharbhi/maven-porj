package com.grocery.handler;

import com.grocery.db.DatabaseManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class ApiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if (method.equals("OPTIONS")) { respond(exchange, 200, "{}"); return; }

        try {
            if (path.equals("/api/products") && method.equals("GET"))          handleGetProducts(exchange);
            else if (path.equals("/api/customers") && method.equals("GET"))    handleGetCustomers(exchange);
            else if (path.equals("/api/customers") && method.equals("POST"))   handleAddCustomer(exchange);
            else if (path.equals("/api/orders") && method.equals("GET"))       handleGetOrders(exchange);
            else if (path.equals("/api/orders") && method.equals("POST"))      handlePlaceOrder(exchange);
            else if (path.equals("/api/cart") && method.equals("GET"))         handleGetCart(exchange);
            else if (path.equals("/api/stats") && method.equals("GET"))        handleStats(exchange);
            else respond(exchange, 404, "{\"error\":\"Not found\"}");
        } catch (Exception e) {
            e.printStackTrace();
            respond(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // GET /api/products
    private void handleGetProducts(HttpExchange exchange) throws Exception {
        Connection conn = DatabaseManager.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT * FROM products ORDER BY category, name"
        );
        JSONArray arr = new JSONArray();
        while (rs.next()) {
            arr.put(new JSONObject()
                .put("id",       rs.getInt("id"))
                .put("name",     rs.getString("name"))
                .put("category", rs.getString("category"))
                .put("price",    rs.getDouble("price"))
                .put("unit",     rs.getString("unit"))
                .put("stock",    rs.getInt("stock"))
                .put("emoji",    rs.getString("emoji")));
        }
        respond(exchange, 200, arr.toString());
    }

    // GET /api/customers
    private void handleGetCustomers(HttpExchange exchange) throws Exception {
        Connection conn = DatabaseManager.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT * FROM customers ORDER BY created_at DESC"
        );
        JSONArray arr = new JSONArray();
        while (rs.next()) {
            arr.put(new JSONObject()
                .put("id",         rs.getInt("id"))
                .put("name",       rs.getString("name"))
                .put("email",      rs.getString("email"))
                .put("phone",      rs.getString("phone"))
                .put("address",    rs.getString("address"))
                .put("created_at", rs.getString("created_at")));
        }
        respond(exchange, 200, arr.toString());
    }

    // POST /api/customers  body: { name, email, phone, address }
    private void handleAddCustomer(HttpExchange exchange) throws Exception {
        JSONObject body = readBody(exchange);
        String name    = body.optString("name", "").trim();
        String email   = body.optString("email", "").trim();
        String phone   = body.optString("phone", "");
        String address = body.optString("address", "");

        if (name.isEmpty() || email.isEmpty()) {
            respond(exchange, 400, "{\"error\":\"name and email are required\"}");
            return;
        }

        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO customers (name, email, phone, address) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone);
        ps.setString(4, address);
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        int id = keys.next() ? keys.getInt(1) : -1;
        respond(exchange, 201, new JSONObject()
            .put("message", "Customer registered")
            .put("id", id)
            .put("name", name)
            .put("email", email).toString());
    }

    // GET /api/orders
    private void handleGetOrders(HttpExchange exchange) throws Exception {
        Connection conn = DatabaseManager.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("""
            SELECT o.id, c.name as customer, o.total, o.status, o.created_at
            FROM orders o JOIN customers c ON o.customer_id = c.id
            ORDER BY o.created_at DESC
        """);
        JSONArray arr = new JSONArray();
        while (rs.next()) {
            arr.put(new JSONObject()
                .put("id",         rs.getInt("id"))
                .put("customer",   rs.getString("customer"))
                .put("total",      rs.getDouble("total"))
                .put("status",     rs.getString("status"))
                .put("created_at", rs.getString("created_at")));
        }
        respond(exchange, 200, arr.toString());
    }

    // POST /api/orders  body: { customer_id, items: [{product_id, quantity}] }
    private void handlePlaceOrder(HttpExchange exchange) throws Exception {
        JSONObject body      = readBody(exchange);
        int customerId       = body.optInt("customer_id", 0);
        JSONArray items      = body.optJSONArray("items");

        if (customerId == 0 || items == null || items.isEmpty()) {
            respond(exchange, 400, "{\"error\":\"customer_id and items are required\"}");
            return;
        }

        Connection conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);
        try {
            // Calculate total
            double total = 0;
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                int productId  = item.getInt("product_id");
                int qty        = item.getInt("quantity");
                ResultSet rs   = conn.createStatement().executeQuery(
                    "SELECT price, stock FROM products WHERE id=" + productId
                );
                if (!rs.next()) throw new Exception("Product " + productId + " not found");
                if (rs.getInt("stock") < qty) throw new Exception("Insufficient stock for product " + productId);
                total += rs.getDouble("price") * qty;
            }

            // Insert order
            PreparedStatement orderPs = conn.prepareStatement(
                "INSERT INTO orders (customer_id, total) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            orderPs.setInt(1, customerId);
            orderPs.setDouble(2, total);
            orderPs.executeUpdate();
            int orderId = orderPs.getGeneratedKeys().getInt(1);

            // Insert cart items & update stock
            PreparedStatement cartPs = conn.prepareStatement(
                "INSERT INTO cart_items (order_id, product_id, quantity, price) VALUES (?,?,?,?)"
            );
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                int productId   = item.getInt("product_id");
                int qty         = item.getInt("quantity");
                ResultSet rs    = conn.createStatement().executeQuery(
                    "SELECT price FROM products WHERE id=" + productId
                );
                rs.next();
                double price = rs.getDouble("price");

                cartPs.setInt(1, orderId);
                cartPs.setInt(2, productId);
                cartPs.setInt(3, qty);
                cartPs.setDouble(4, price);
                cartPs.addBatch();

                // Reduce stock
                conn.createStatement().execute(
                    "UPDATE products SET stock = stock - " + qty + " WHERE id=" + productId
                );
            }
            cartPs.executeBatch();
            conn.commit();

            respond(exchange, 201, new JSONObject()
                .put("message",  "Order placed successfully")
                .put("order_id", orderId)
                .put("total",    total).toString());

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // GET /api/cart?order_id=X
    private void handleGetCart(HttpExchange exchange) throws Exception {
        String query   = exchange.getRequestURI().getQuery();
        int orderId    = 0;
        if (query != null && query.startsWith("order_id=")) {
            orderId = Integer.parseInt(query.split("=")[1]);
        }
        if (orderId == 0) {
            respond(exchange, 400, "{\"error\":\"order_id is required\"}");
            return;
        }

        Connection conn = DatabaseManager.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("""
            SELECT ci.id, p.name, p.emoji, ci.quantity, ci.price,
                   (ci.quantity * ci.price) as subtotal
            FROM cart_items ci JOIN products p ON ci.product_id = p.id
            WHERE ci.order_id = %d
        """.formatted(orderId));

        JSONArray arr = new JSONArray();
        double total  = 0;
        while (rs.next()) {
            double sub = rs.getDouble("subtotal");
            total += sub;
            arr.put(new JSONObject()
                .put("product",  rs.getString("name"))
                .put("emoji",    rs.getString("emoji"))
                .put("quantity", rs.getInt("quantity"))
                .put("price",    rs.getDouble("price"))
                .put("subtotal", sub));
        }
        respond(exchange, 200, new JSONObject()
            .put("order_id", orderId)
            .put("items", arr)
            .put("total", total).toString());
    }

    // GET /api/stats
    private void handleStats(HttpExchange exchange) throws Exception {
        Connection conn = DatabaseManager.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("""
            SELECT
              (SELECT COUNT(*) FROM customers) as total_customers,
              (SELECT COUNT(*) FROM orders)    as total_orders,
              (SELECT COUNT(*) FROM products)  as total_products,
              (SELECT COALESCE(SUM(total),0) FROM orders WHERE status='pending')   as pending_revenue,
              (SELECT COALESCE(SUM(total),0) FROM orders WHERE status='delivered') as delivered_revenue
        """);
        rs.next();
        respond(exchange, 200, new JSONObject()
            .put("total_customers",    rs.getInt("total_customers"))
            .put("total_orders",       rs.getInt("total_orders"))
            .put("total_products",     rs.getInt("total_products"))
            .put("pending_revenue",    rs.getDouble("pending_revenue"))
            .put("delivered_revenue",  rs.getDouble("delivered_revenue")).toString());
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private JSONObject readBody(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return body.isBlank() ? new JSONObject() : new JSONObject(body);
    }

    private void respond(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
