package com.grocery.db;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:grocery.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA journal_mode=WAL");
            connection.createStatement().execute("PRAGMA foreign_keys=ON");
        }
        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        // Customers table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS customers (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT    NOT NULL,
                email     TEXT    UNIQUE NOT NULL,
                phone     TEXT,
                address   TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Products table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS products (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT    NOT NULL,
                category    TEXT    NOT NULL,
                price       REAL    NOT NULL,
                unit        TEXT    NOT NULL,
                stock       INTEGER DEFAULT 100,
                emoji       TEXT,
                created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Orders table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS orders (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                total       REAL    NOT NULL,
                status      TEXT    DEFAULT 'pending',
                created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES customers(id)
            )
        """);

        // Cart items table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS cart_items (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id    INTEGER NOT NULL,
                product_id  INTEGER NOT NULL,
                quantity    INTEGER NOT NULL,
                price       REAL    NOT NULL,
                FOREIGN KEY (order_id)   REFERENCES orders(id),
                FOREIGN KEY (product_id) REFERENCES products(id)
            )
        """);

        // Seed products if empty
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
        if (rs.next() && rs.getInt(1) == 0) {
            seedProducts(conn);
        }

        System.out.println("✅ Database initialized: grocery.db");
    }

    private static void seedProducts(Connection conn) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, unit, stock, emoji) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        Object[][] products = {
            {"Organic Tomatoes",   "Vegetables", 49.0,  "500g",    150, "🍅"},
            {"Fresh Spinach",      "Vegetables", 35.0,  "250g",    120, "🥬"},
            {"Broccoli",           "Vegetables", 55.0,  "500g",    100, "🥦"},
            {"Carrots",            "Vegetables", 40.0,  "500g",    200, "🥕"},
            {"Avocados",           "Fruits",     89.0,  "2 pcs",    80, "🥑"},
            {"Bananas",            "Fruits",     35.0,  "6 pcs",   300, "🍌"},
            {"Apples",             "Fruits",     99.0,  "1kg",     180, "🍎"},
            {"Strawberries",       "Fruits",     120.0, "250g",     90, "🍓"},
            {"Full Cream Milk",    "Dairy",      62.0,  "1 litre", 200, "🥛"},
            {"Cheddar Cheese",     "Dairy",      180.0, "200g",     60, "🧀"},
            {"Greek Yogurt",       "Dairy",      95.0,  "400g",    110, "🍶"},
            {"Butter",             "Dairy",      55.0,  "100g",    130, "🧈"},
            {"Sourdough Bread",    "Bakery",     85.0,  "400g",     50, "🍞"},
            {"Croissants",         "Bakery",     120.0, "4 pcs",    40, "🥐"},
            {"Orange Juice",       "Beverages",  110.0, "1 litre", 100, "🧃"},
            {"Sparkling Water",    "Beverages",  45.0,  "750ml",   200, "💧"},
        };

        for (Object[] p : products) {
            ps.setString(1, (String) p[0]);
            ps.setString(2, (String) p[1]);
            ps.setDouble(3, (Double) p[2]);
            ps.setString(4, (String) p[3]);
            ps.setInt(5,    (Integer) p[4]);
            ps.setString(6, (String) p[5]);
            ps.addBatch();
        }
        ps.executeBatch();
        System.out.println("✅ Seeded " + products.length + " products");
    }
}
