package com.griddynamics.pift;


import com.griddynamics.pift.Entities.Department;
import com.griddynamics.pift.Entities.Entity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityTest {
    Entity entity;
    EntityManager entityManager;

    @BeforeAll
    void before() {
        entityManager = getEntityManager();
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
    void getList() {
        Entity entity = new Entity();
        entity.setName("snake");
        entity.setCount(BigDecimal.valueOf(345742));
        List<Entity> list = entityManager.getList(entity);
        Entity actualEntity = list.get(0);

        log.debug(list.toString());

        Assertions.assertEquals(6, actualEntity.getId());
        Assertions.assertEquals(425821, actualEntity.getNumber());
        Assertions.assertEquals("snake", actualEntity.getName());
        Assertions.assertNull(actualEntity.getAge());
        Assertions.assertEquals(new BigDecimal(345742), actualEntity.getCount());
        Assertions.assertNotNull(actualEntity.getDepartment());
    }

    @Test
    void getById(){
        Entity entity = entityManager.getById(Entity.class, 6).orElse(null);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(6, entity.getId());
        Assertions.assertEquals(425821, entity.getNumber());
        Assertions.assertEquals("snake", entity.getName());
        Assertions.assertNull(entity.getAge());
        Assertions.assertEquals(new BigDecimal(345742), entity.getCount());
        Assertions.assertNotNull(entity.getDepartment());
    }

    @Test
    void main() {
        entityManager.flush();
    }

    EntityManager getEntityManager(){
        try(Connection con = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
            Statement stmt = con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE entity " +
                    "(id bigint primary key , number bigint, name varchar(50), age int, count decimal, localdatetime timestamp, " +
                    " date date, timestamp timestamp, localdate date, dept_id bigint); " +
                    "CREATE TABLE department (id bigint primary key , location varchar(50));");
            stmt.executeUpdate("INSERT INTO entity (id, number, name, count, dept_id) VALUES (6, 425821, 'snake', 345742, 10)");
        }catch (Exception e){
            throw new IllegalArgumentException("", e);
        }
        return new EntityManager("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
    }
}