package com.grocery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationTest {
    @Test
    public void testWelcomeMessage() {
        MainApplication app = new MainApplication();
        assertEquals("Welcome to Online Grocery Delivery!", app.getWelcomeMessage());
    }
}
