package com.griddynamics.uspanov.test.Entities;

import com.griddynamics.uspanov.test.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartmentTest {
    Department department;
    EntityManager entityManager = new EntityManager();

    @BeforeEach
    void before(){
        department = entityManager.create(Department.class);
    }

    @Test
    void getId() {
        Assertions.assertNotNull(department.getId());
    }

    @Test
    void getLocation() {
        Assertions.assertNotNull(department.getLocation());
    }

    @Test
    void main(){
        entityManager.flush();
    }
}