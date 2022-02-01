package com.griddynamics.pift;

import com.github.javafaker.Faker;
import com.griddynamics.pift.Entities.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

class FieldCreatorManagerTest {
    FieldCreatorManager fieldCreatorManager = new FieldCreatorManager();
    Faker faker = new Faker();

    @Test
    void createValue() {
        try {
            Assertions.assertTrue(fieldCreatorManager.createValue(Entity.class.getDeclaredField("date"))
                    .getClass().isAssignableFrom(Date.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createValue test method", e);
        }
    }

    @Test
    void getForeignKeyTableName() {
        try {
            Assertions.assertTrue(fieldCreatorManager.getForeignKeyTableName(Entity.class.getDeclaredField("department")).isPresent());
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in getForeignKeyTableName test method", e);

        }
    }

    @Test
    void addValueGenerator() {
        fieldCreatorManager.addValueGenerator(java.util.Date.class, (a) -> faker.date().birthday().getTime());
        Assertions.assertTrue(fieldCreatorManager.containsInFieldsMapping(java.util.Date.class));
    }

    @Test
    void testAddValueGenerator() {
        try {
            fieldCreatorManager.addValueGenerator(Entity.class.getDeclaredField("department"), (field) -> "");
            Assertions.assertTrue(fieldCreatorManager.containsInUserCreatorByField(Entity.class.getDeclaredField("department")));
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in testAddValueGenerator test method", e);
        }
    }

    @Test
    void containsInFieldsMapping() {
        Assertions.assertTrue(fieldCreatorManager.containsInFieldsMapping(LocalDate.class));
    }

    @Test
    void existInProperties() {
        try {
            Assertions.assertTrue(fieldCreatorManager.existInProperties(Entity.class.getDeclaredField("date")));
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in existInProperties test method", e);
        }
    }
}