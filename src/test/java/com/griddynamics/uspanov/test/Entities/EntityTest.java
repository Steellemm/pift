package com.griddynamics.uspanov.test.Entities;

import com.griddynamics.uspanov.test.Entities.Entity;
import com.griddynamics.uspanov.test.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntityTest {
    Entity entity;
    EntityManager entityManager = new EntityManager();

    @BeforeEach
    void before(){
        entity = entityManager.create(Entity.class);
    }

    @Test
    void getName() {
        Assertions.assertNotNull(entity.getName());
    }

    @Test
    void getNumber() {
        Assertions.assertNotEquals(null, entity.getNumber());
    }

    @Test
    void getAge() {
//        Assertions.assertNotEquals(null, entity.getAge());
    }

    @Test
    void getCount() {
        Assertions.assertNotEquals(null, entity.getCount());
    }

    @Test
    void test(){
        System.out.println(entity.getAddress());
    }

    @Test
    void main() {
        entityManager.flush();
    }
}