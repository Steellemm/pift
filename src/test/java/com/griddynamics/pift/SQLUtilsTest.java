package com.griddynamics.pift;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SQLUtilsTest {
    TestClass testClass = new TestClass("text", 10);

    @Test
    void readField() {
        Assertions.assertEquals("10", SQLUtils.readField(testClass.getClass().getDeclaredFields()[1], testClass));
        Assertions.assertEquals("'text'", SQLUtils.readField(testClass.getClass().getDeclaredFields()[0], testClass));
    }

    @Test
    void createQueryForInsert() {
        Assertions.assertNotNull(SQLUtils.createQueryForInsert(testClass));
    }

    private static class TestClass{
        String text;
        int number;

        public TestClass(String text, int number) {
            this.text = text;
            this.number = number;
        }
    }
}