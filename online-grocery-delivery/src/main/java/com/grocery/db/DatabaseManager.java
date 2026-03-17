package com.grocery.db;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:/app/data/grocery.db";
    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA journal_mode=WAL");
                st.execute("PRAGMA foreign_keys=ON");
                st.execute("PRAGMA busy_timeout=5000");
                st.execute("PRAGMA synchronous=NORMAL");
            }
        }
        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        Connection conn = getConnection();

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    name       TEXT    NOT NULL,
                    email      TEXT    UNIQUE NOT NULL,
                    phone      TEXT,
                    address    TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    name       TEXT    NOT NULL,
                    category   TEXT    NOT NULL,
                    price      REAL    NOT NULL,
                    unit       TEXT    NOT NULL,
                    stock      INTEGER DEFAULT 100,
                    emoji      TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

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

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cart_items (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id   INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    quantity   INTEGER NOT NULL,
                    price      REAL    NOT NULL,
                    FOREIGN KEY (order_id)   REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
            """);
        }

        boolean isEmpty;
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            isEmpty = rs.next() && rs.getInt(1) == 0;
        }

        if (isEmpty) seedProducts(conn);

        System.out.println("✅ Database initialized: /app/data/grocery.db");
    }

    private static void seedProducts(Connection conn) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, unit, stock, emoji) VALUES (?,?,?,?,?,?)";

        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Object[][] products = {
                {"Organic Tomatoes", "Vegetables", 49.0,  "500g",    150, "🍅"},
                {"Fresh Spinach",    "Vegetables", 35.0,  "250g",    120, "🥬"},
                {"Broccoli",         "Vegetables", 55.0,  "500g",    100, "🥦"},
                {"Carrots",          "Vegetables", 40.0,  "500g",    200, "🥕"},
                {"Avocados",         "Fruits",     89.0,  "2 pcs",    80, "🥑"},
                {"Bananas",          "Fruits",     35.0,  "6 pcs",   300, "🍌"},
                {"Apples",           "Fruits",     99.0,  "1kg",     180, "🍎"},
                {"Strawberries",     "Fruits",     120.0, "250g",     90, "🍓"},
                {"Full Cream Milk",  "Dairy",      62.0,  "1 litre", 200, "🥛"},
                {"Cheddar Cheese",   "Dairy",      180.0, "200g",     60, "🧀"},
                {"Greek Yogurt",     "Dairy",      95.0,  "400g",    110, "🍶"},
                {"Butter",           "Dairy",      55.0,  "100g",    130, "🧈"},
                {"Sourdough Bread",  "Bakery",     85.0,  "400g",     50, "🍞"},
                {"Croissants",       "Bakery",     120.0, "4 pcs",    40, "🥐"},
                {"Orange Juice",     "Beverages",  110.0, "1 litre", 100, "🧃"},
                {"Sparkling Water",  "Beverages",  45.0,  "750ml",   200, "💧"},
            };

            for (Object[] p : products) {
                ps.setString(1, (String)  p[0]);
                ps.setString(2, (String)  p[1]);
                ps.setDouble(3, (Double)  p[2]);
                ps.setString(4, (String)  p[3]);
                ps.setInt(5,    (Integer) p[4]);
                ps.setString(6, (String)  p[5]);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        System.out.println("✅ Seeded 16 products");
    }
}
