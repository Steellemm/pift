package com.griddynamics.pift.Entities;

import com.griddynamics.pift.Entities.Entities.Department;
import com.griddynamics.pift.Entities.Entities.Entity;
import com.griddynamics.pift.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class EntityTest {
    Entity entity;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

    @BeforeEach
    void before(){
        entityManager.create(Department.class);
        entity = entityManager.create(Entity.class);
    }

    @Test
    void getName() {
        Assertions.assertNotNull(entity.getName());
    }

    @Test
    void getNumber() {
        Assertions.assertNotNull(entity.getNumber());
    }

    @Test
    @Disabled
    void getAge() {
        Assertions.assertNotNull(entity.getAge());
    }

    @Test
    void getCount() {
        Assertions.assertNotNull(entity.getCount());
    }

    @Test
    void test(){
    }

    @Test
    void main() {
        entityManager.flush();
    }
}