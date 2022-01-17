package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SQLUtilsTest {
    Department department;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
    TestClass testClass = new TestClass("text", 10);

    @BeforeEach
    void before(){
        department = entityManager.create(Department.class);
    }

    @Test
    void readField() {
        Assertions.assertEquals("10", SQLUtils.readField(testClass.getClass().getDeclaredFields()[1], testClass));
        Assertions.assertEquals("'text'", SQLUtils.readField(testClass.getClass().getDeclaredFields()[0], testClass));
    }

    @Test
    void createQueryForInsert() {
        Assertions.assertNotNull(SQLUtils.createQueryForInsert(department));
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