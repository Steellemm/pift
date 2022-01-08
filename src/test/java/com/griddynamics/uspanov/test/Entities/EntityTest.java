package com.griddynamics.uspanov.test.Entities;

import com.griddynamics.uspanov.test.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class EntityTest {
    Entity entity;
    EntityManager entityManager = new EntityManager();

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