package com.example.demo.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorControllerTest {

    private final CalculatorController controller = new CalculatorController();

    @Test
    void testAddition() {
        assertEquals(5.0, controller.evaluate("2+3"));
    }

    @Test
    void testSubtraction() {
        assertEquals(4.0, controller.evaluate("7 - 3"));
    }

    @Test
    void testMultiplication() {
        assertEquals(12.0, controller.evaluate("4*3"));
    }

    @Test
    void testDivision() {
        assertEquals(2.5, controller.evaluate("5 / 2"));
    }

    @Test
    void testDivisionByZero() {
        assertEquals(Double.NaN, controller.evaluate("5/0"));
    }

    @Test
    void testInvalidExpression() {
        assertThrows(IllegalArgumentException.class, () -> controller.evaluate("2++2"));
    }

    @Test
    void testUnknownOperator() {
        assertThrows(IllegalArgumentException.class, () -> controller.evaluate("2^2"));
    }

    @Test
    void testWhitespaceHandling() {
        assertEquals(9.0, controller.evaluate(" 4 + 5 "));
    }
}
