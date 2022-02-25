package com.griddynamics.pift.utils;

import com.griddynamics.pift.entities.Department;
import com.griddynamics.pift.utils.ReflectionUtils;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.Arrays;

class ReflectionUtilsTest {
    TestClass testClass = new TestClass("text");

    @Test
    void getTableName() {
        Assertions.assertEquals("department", ReflectionUtils.getTableName(Department.class));
    }

    @Test
    void getColumnFields() {
        Assertions.assertEquals(1, ReflectionUtils.getColumnFields(testClass.getClass()).count());
    }

    @Test
    void getFieldValue() {
        Assertions.assertEquals("text", ReflectionUtils.getFieldValue(testClass.getClass().getDeclaredFields()[0], testClass));
    }

    @Test
    void setFieldValue() {
        ReflectionUtils.setFieldValue(testClass, getFirstStringField(testClass), "value");
        Assertions.assertEquals("value", ReflectionUtils.getFieldValue(getFirstStringField(testClass), testClass));
    }

    @Test
    void createInstance() {
        Assertions.assertNotNull(ReflectionUtils.createInstance(Department.class));
    }

    private Field getFirstStringField(Object obj){
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(String.class))
                .findFirst().get();
    }


    private static class TestClass{
        String text;

        public TestClass(String text) {
            this.text = text;
        }
    }
}