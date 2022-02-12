package com.griddynamics.pift;

import com.github.javafaker.Faker;
import com.griddynamics.pift.Entities.Entity;
import com.griddynamics.pift.fieldsMapping.FieldValue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

class FieldCreatorManagerTest {
    FieldCreatorManager fieldCreatorManager = new FieldCreatorManager();
    Faker faker = new Faker();

    @Test
    @SneakyThrows
    void createValue() {
        Assertions.assertTrue(fieldCreatorManager.createValue(Entity.class.getDeclaredField("date"))
                .getClass().isAssignableFrom(Date.class));
    }

    @Test
    @SneakyThrows
    void getForeignKeyTableName() {
        Assertions.assertTrue(fieldCreatorManager.getForeignKey(Entity.class.getDeclaredField("department")).isPresent());
    }

    @Test
    void addValueGenerator() {
        fieldCreatorManager.addValueGenerator(java.util.Date.class, new FieldValue<java.util.Date>() {
            @Override
            public java.util.Date generate() {
                return null;
            }

            @Override
            public Class<java.util.Date> getType() {
                return null;
            }

            @Override
            public java.util.Date getValueFromString(String value) {
                return null;
            }
        });
        Assertions.assertTrue(fieldCreatorManager.containsInFieldsMapping(java.util.Date.class));
    }

    @Test
    @SneakyThrows
    void testAddValueGenerator() {
        fieldCreatorManager.addValueGenerator(Entity.class.getDeclaredField("department"), (field) -> "");
        Assertions.assertTrue(fieldCreatorManager.containsInUserCreatorByField(Entity.class.getDeclaredField("department")));
    }

    @Test
    void containsInFieldsMapping() {
        Assertions.assertTrue(fieldCreatorManager.containsInFieldsMapping(LocalDate.class));
    }

    @Test
    @SneakyThrows
    void existInProperties() {
        Assertions.assertTrue(fieldCreatorManager.existInProperties(Entity.class.getDeclaredField("date")));
    }
}