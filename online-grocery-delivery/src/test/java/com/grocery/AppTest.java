
package com.grocery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    @Test
    public void testWelcomeMessage() {
        App app = new App();
        assertEquals("Welcome to Online Grocery Delivery!", app.getWelcomeMessage());
    }
}
