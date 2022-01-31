package com.griddynamics.pift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.Entities.Department;
import com.griddynamics.pift.Entities.Entity;
import com.griddynamics.pift.model.PiftProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
@Slf4j
class EntityTest {
    Entity entity;
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

    @BeforeEach
    void before() {
        Department department = entityManager.create(Department.class);
        entity = entityManager.create(Entity.class);
        log.debug(entity.toString());
        log.debug(department.toString());
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
    void test() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            PiftProperties piftProperties = mapper.readValue(new File("src/main/resources/pift.yaml"), PiftProperties.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void main() {
        entityManager.flush();
    }
}