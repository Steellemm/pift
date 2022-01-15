package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerTest {
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

    @Test
    void create() {
        Assertions.assertNotNull(entityManager.create(Department.class));
    }

    @Test
    void flush() {
        entityManager.create(Department.class);
        Assertions.assertEquals(1, entityManager.flush());
    }
}