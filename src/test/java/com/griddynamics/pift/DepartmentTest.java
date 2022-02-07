package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepartmentTest {
    Department department;
    EntityManager entityManager;

    @BeforeAll
    void before() {
        entityManager = getEntityManager();
        department = entityManager.create(Department.class);
    }

    @Test
    void getId() {
        Assertions.assertNotNull(department.getId());
    }

    @Test
    void getLocation() {
        Assertions.assertNotNull(department.getLocation());
    }

    @Test
    void main(){
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