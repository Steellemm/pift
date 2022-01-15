package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class EntityUtilsTest {
    List<Object> createdEntityList = new ArrayList<>();

    @Test
    void create() {
        Assertions.assertNotNull(EntityUtils.create(Department.class, createdEntityList));
    }
}