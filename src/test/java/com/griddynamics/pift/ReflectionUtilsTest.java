package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {
    Department department;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
    TestClass testClass = new TestClass("text");

    @BeforeEach
    void before(){
        department = entityManager.create(Department.class);
    }

    @Test
    @Disabled
    void checkIfFieldFilled() {
    }

    @Test
    void getTableName() {
        Assertions.assertEquals("department", ReflectionUtils.getTableName(Department.class));
    }

    @Test
    void getColumnFields() {
        Assertions.assertTrue(ReflectionUtils.getColumnFields(department).findFirst().isPresent());
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
                .filter(field -> field.getType().getSimpleName().equals("String"))
                .findFirst().get();
    }


    private static class TestClass{
        String text;

        public TestClass(String text) {
            this.text = text;
        }
    }
}