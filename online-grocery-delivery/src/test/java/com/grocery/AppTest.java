package com.grocery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    @Test
    public void testWelcomeMessage() {
        // Change 'App' to 'MainApplication'
        MainApplication app = new MainApplication();
        assertEquals("Welcome to Online Grocery Delivery!", app.getWelcomeMessage());
    }
}
