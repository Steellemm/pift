package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class EntityUtilsTest {
    List<Object> createdEntityList = new ArrayList<>();

    @Test
    void create() {
        Department department = EntityUtils.create(Department.class, createdEntityList);
        Assertions.assertNotNull(department.getId());
        Assertions.assertNotNull(department.getLocation());
    }
}