package com.griddynamics.pift.utils;

import com.griddynamics.pift.entities.CKEntity;
import com.griddynamics.pift.entities.CompositeKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SQLUtilsTest {

    @Test
    void createQueryForInsert() {
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setName("name");
        compositeKey.setDepartmentId(123L);
        CKEntity entityWithId = new CKEntity();
        entityWithId.setId(compositeKey);
        entityWithId.setInfo("info");
        Assertions.assertEquals("INSERT INTO ck_entity (departmentId, name, info) values (123, 'name', 'info')",
                SQLUtils.insert(entityWithId));
    }

    @Test
    void createQueryForSelect() {
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setName("name");
        compositeKey.setDepartmentId(123L);
        CKEntity entityWithId = new CKEntity();
        entityWithId.setId(compositeKey);
        entityWithId.setInfo("info");
        Assertions.assertEquals("SELECT * FROM ck_entity WHERE departmentId = 123 AND name = 'name' AND info = 'info'",
                SQLUtils.select(entityWithId));
    }
}