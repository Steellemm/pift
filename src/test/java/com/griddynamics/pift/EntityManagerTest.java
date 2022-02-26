package com.griddynamics.pift;

import com.griddynamics.pift.entities.Department;
import com.griddynamics.pift.entities.Entity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class EntityManagerTest {

    private final EntityManager entityManager = EntityManager.builder()
            .setUrl("")
            .setPassword("")
            .setUser("")
            .addEntityPackage("com.griddynamics.pift.entities")
            .build();

    @Test
    void testCreateByTableName() {
        Department department = (Department) entityManager.create("department");
        Entity entity = (Entity) entityManager.create("entity");
        Assertions.assertEquals(entity.getDepartment(), department.getId());
    }

    @Test
    void testCreate() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "100");
        map.put("number", "500");
        map.put("name", "August");
        map.put("age", "1");
        map.put("date", "2022-01-01");
        map.put("timestamp", "2022-02-05T13:11:58.782197Z");
        map.put("localDate", "2022-01-02");
        map.put("localDateTime", "2022-01-02T00:51:50.194084700");
        map.put("count", "1000000");
        map.put("department", "0");
        Department department = entityManager.create(Department.class);
        Entity entity = entityManager.create(Entity.class, map);
        Assertions.assertEquals(100, entity.getId());
        Assertions.assertEquals("August", entity.getName());
        Assertions.assertEquals(department.getId(), entity.getDepartment());
        Assertions.assertEquals(500, entity.getNumber());
        Assertions.assertEquals(1, entity.getAge());
        Assertions.assertEquals(new BigDecimal(1000000), entity.getCount());
        Assertions.assertEquals("2022-01-01T00:00:00Z", entity.getDate().toInstant().toString());
        Assertions.assertEquals(LocalDate.of(2022, 1, 2), entity.getLocalDate());
        Assertions.assertEquals("2022-02-05T13:11:58.782197Z", entity.getTimestamp().toInstant().toString());
        Assertions.assertEquals(LocalDateTime.parse("2022-01-02T00:51:50.194084700"), entity.getLocalDateTime());
    }

}