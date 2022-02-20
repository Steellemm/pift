package com.griddynamics.pift;

import com.griddynamics.pift.entities.Entity;
import com.griddynamics.pift.creator.TypeValue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

class FieldCreatorManagerTest {
    private final FieldCreatorManager fieldCreatorManager = new FieldCreatorManager(new EntityMap());

    @Test
    @SneakyThrows
    void createValue() {
        Assertions.assertTrue(fieldCreatorManager.createValue(Entity.class.getDeclaredField("date"))
                .getClass().isAssignableFrom(Date.class));
    }

    @Test
    void addValueGenerator() {
        fieldCreatorManager.addValueGenerator(NewFieldClass.class, new TypeValue<NewFieldClass>() {
            @Override
            public NewFieldClass generate() {
                return null;
            }

            @Override
            public Class<NewFieldClass> getType() {
                return null;
            }

            @Override
            public NewFieldClass parse(String value) {
                return null;
            }
        });
        Assertions.assertTrue(fieldCreatorManager.supportsType(NewFieldClass.class));
    }

    private static class NewFieldClass {
    }

    @Test
    @SneakyThrows
    void testAddValueGenerator() {
        fieldCreatorManager.addValueGenerator(Entity.class.getDeclaredField("department"), (field) -> "");
        Assertions.assertTrue(fieldCreatorManager.containsInUserCreatorByField(Entity.class.getDeclaredField("department")));
    }

    @Test
    void containsInFieldsMapping() {
        Assertions.assertTrue(fieldCreatorManager.supportsType(LocalDate.class));
    }
}