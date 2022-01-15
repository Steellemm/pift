package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SQLUtilsTest {
    Department department;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

    @BeforeEach
    void before(){
        department = entityManager.create(Department.class);
    }

    @Test
    void readField() {
        Assertions.assertNotNull(SQLUtils.readField(department.getClass().getDeclaredFields()[0], department));
    }

    @Test
    void createQueryForInsert() {
        Assertions.assertNotNull(SQLUtils.createQueryForInsert(department));
    }
}