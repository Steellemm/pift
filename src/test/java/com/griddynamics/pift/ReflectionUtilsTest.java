package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {
    Department department;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

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
        Assertions.assertNotNull(ReflectionUtils.getTableName(Department.class));
    }

    @Test
    void getColumnFields() {
        Assertions.assertTrue(ReflectionUtils.getColumnFields(department).findFirst().isPresent());
    }

    @Test
    void getFieldValue() {
        Assertions.assertNotNull(ReflectionUtils.getFieldValue(department.getClass().getDeclaredFields()[0], department));
    }

    @Test
    void setFieldValue() {
        ReflectionUtils.setFieldValue(department, getFirstStringField(department), "value");
        Assertions.assertNotNull(ReflectionUtils.getFieldValue(getFirstStringField(department), department));
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
}