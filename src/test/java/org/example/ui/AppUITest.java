package org.example.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUITest {
    @Test
    void testDOIT() {
        assertEquals(2, new AppUI().doit());
    }
}