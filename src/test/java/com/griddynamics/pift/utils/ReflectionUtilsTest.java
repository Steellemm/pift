package com.griddynamics.pift.utils;

import com.griddynamics.pift.entities.CKEntity;
import com.griddynamics.pift.entities.CompositeKey;
import com.griddynamics.pift.entities.Department;
import com.griddynamics.pift.utils.ReflectionUtils;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class ReflectionUtilsTest {
    TestClass testClass = new TestClass("text");

    @Test
    void getTableName() {
        Assertions.assertEquals("department", ReflectionUtils.getTableName(Department.class));
    }

    @Test
    void createEntityWithId() {
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setName("name");
        compositeKey.setDepartmentId(123L);
        CKEntity entityWithId = ReflectionUtils.createEntityWithId(CKEntity.class, compositeKey);
        Assertions.assertEquals(123L, entityWithId.getId().getDepartmentId());
        Assertions.assertEquals("name", entityWithId.getId().getName());
        Assertions.assertNull(entityWithId.getInfo());
    }

    @Test
    void getValuesByColumnName() {
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setName("name");
        compositeKey.setDepartmentId(123L);
        CKEntity entityWithId = ReflectionUtils.createEntityWithId(CKEntity.class, compositeKey);
        entityWithId.setInfo("info");
        Map<String, String> valuesByColumnName = ReflectionUtils.getValuesByColumnName(entityWithId);
        Assertions.assertEquals("123", valuesByColumnName.get("departmentId"));
        Assertions.assertEquals("'name'", valuesByColumnName.get("name"));
        Assertions.assertEquals("'info'", valuesByColumnName.get("info"));
    }


    @Test
    void getColumnFields() {
        Set<String> fieldsName = ReflectionUtils.getColumnFields(CKEntity.class)
                .map(ReflectionUtils::getColumnName)
                .collect(Collectors.toSet());
        Assertions.assertEquals(2, fieldsName.size());
        Assertions.assertTrue(fieldsName.contains("info"));
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