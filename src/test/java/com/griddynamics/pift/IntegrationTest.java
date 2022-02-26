package com.griddynamics.pift;

import com.griddynamics.pift.entities.Department;
import com.griddynamics.pift.entities.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class IntegrationTest {

    private final EntityManager entityManager = EntityManager.builder()
            .setUrl("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1")
            .setPassword("")
            .setUser("")
            .build();

    @BeforeAll
    static void createTables() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS entity " +
                    "(id bigint primary key , number bigint, name varchar(50), age int, count decimal, localdatetime timestamp, " +
                    " date date, timestamp timestamp, localdate date, dept_id bigint, address varchar(50)); " +
                    "CREATE TABLE IF NOT EXISTS department (id bigint primary key , location varchar(50));");
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in getEntityManager method", e);
        }
    }


    @BeforeEach
    void clear() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DELETE FROM entity");
            stmt.executeUpdate("DELETE FROM department");
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in clear method", e);
        }
    }

    private void addDataToDatabase() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate("INSERT INTO entity (id, number, name, count, dept_id, localDate, timestamp, localDateTime) " +
                    "VALUES (6, 425821, 'snake', 345742, 10, '2022-01-01', '2022-02-02 13:11:58.782197', '2022-02-01T00:51:50.194084700')");
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in addDataToDatabase method", e);
        }
    }

    @Test
    void getList() {
        addDataToDatabase();
        Entity entity = new Entity();
        entity.setName("snake");
        entity.setCount(BigDecimal.valueOf(345742));
        List<Entity> list = entityManager.getList(entity);
        Entity actualEntity = list.get(0);

        Assertions.assertEquals(6, actualEntity.getId());
        Assertions.assertEquals(425821, actualEntity.getNumber());
        Assertions.assertEquals("snake", actualEntity.getName());
        Assertions.assertNull(actualEntity.getAge());
        Assertions.assertEquals(new BigDecimal(345742), actualEntity.getCount());
        Assertions.assertNotNull(actualEntity.getDepartment());
    }

    @Test
    void getById() {
        addDataToDatabase();
        Entity entity = entityManager.getById(Entity.class, 6L).orElse(null);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(6, entity.getId());
        Assertions.assertEquals(425821, entity.getNumber());
        Assertions.assertEquals("snake", entity.getName());
        Assertions.assertNull(entity.getAge());
        Assertions.assertEquals(new BigDecimal(345742), entity.getCount());
        Assertions.assertNotNull(entity.getDepartment());
    }

    @Test
    void flush() {
        Department department = entityManager.create(Department.class);
        Entity entity = entityManager.create(Entity.class);

        entityManager.flush();

        Entity actualEntity = entityManager.getById(Entity.class, entity.getId())
                .orElseThrow(() -> new IllegalArgumentException("No such element in the database"));

        Department actualDepartment = entityManager.getById(Department.class, department.getId())
                .orElseThrow(() -> new IllegalArgumentException("No such element in the database"));

        Assertions.assertEquals(entity.getId(), actualEntity.getId());
        Assertions.assertEquals(entity.getCount(), actualEntity.getCount());
        Assertions.assertEquals(entity.getDepartment(), actualEntity.getDepartment());
        Assertions.assertEquals(entity.getNumber(), actualEntity.getNumber());
        Assertions.assertEquals(entity.getName(), actualEntity.getName());
        Assertions.assertEquals(entity.getAge(), actualEntity.getAge());
        Assertions.assertEquals(entity.getLocalDate(), actualEntity.getLocalDate());
        Assertions.assertEquals(entity.getLocalDateTime(), actualEntity.getLocalDateTime());
        Assertions.assertEquals(entity.getTimestamp(), actualEntity.getTimestamp());

        Assertions.assertEquals(department.getId(), actualDepartment.getId());
        Assertions.assertEquals(department.getLocation(), actualDepartment.getLocation());
    }

}
