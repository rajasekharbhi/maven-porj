package com.grocery;

public class MainApplication {
    
    public String getWelcomeMessage() {
        return "Welcome to Online Grocery Delivery!";
    }

    public static void main(String[] args) {
        MainApplication app = new MainApplication();
        System.out.println(app.getWelcomeMessage());
    }
}
