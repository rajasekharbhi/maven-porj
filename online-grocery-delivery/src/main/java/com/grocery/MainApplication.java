package com.grocery;

import com.grocery.db.DatabaseManager;
import com.grocery.handler.ApiHandler;
import com.grocery.handler.FrontendHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class MainApplication {

    public static void main(String[] args) throws Exception {
        int port = 9000;

        // Initialize SQLite database & seed products
        DatabaseManager.initializeDatabase();

        // Start HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/",    new FrontendHandler());  // Serves the UI
        server.createContext("/api", new ApiHandler());       // REST API
        server.setExecutor(null);
        server.start();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  🛒 FreshCart is running!            ║");
        System.out.println("║  http://localhost:" + port + "              ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
        System.out.println("API Endpoints:");
        System.out.println("  GET  /api/products");
        System.out.println("  GET  /api/customers");
        System.out.println("  POST /api/customers");
        System.out.println("  GET  /api/orders");
        System.out.println("  POST /api/orders");
        System.out.println("  GET  /api/cart?order_id=X");
        System.out.println("  GET  /api/stats");
    }
}
