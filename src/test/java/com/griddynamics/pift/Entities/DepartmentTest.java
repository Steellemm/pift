package com.griddynamics.pift.Entities;

import com.griddynamics.pift.Entities.Entities.Department;
import com.griddynamics.pift.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartmentTest {
    Department department;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

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