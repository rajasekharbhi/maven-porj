package com.grocery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationTest {

    @Test
    public void testApplicationStarts() {
        // Verify MainApplication class loads without errors
        assertDoesNotThrow(() -> {
            Class.forName("com.grocery.MainApplication");
        });
    }

    @Test
    public void testDatabaseManagerClassExists() {
        assertDoesNotThrow(() -> {
            Class.forName("com.grocery.db.DatabaseManager");
        });
    }
}
