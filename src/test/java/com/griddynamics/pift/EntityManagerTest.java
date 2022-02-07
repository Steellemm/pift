package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.*;

import java.sql.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityManagerTest {
    EntityManager entityManager;

    @BeforeAll
    void before() {
        entityManager = getEntityManager();
    }

    @Test
    void create() {
        Assertions.assertNotNull(entityManager.create(Department.class));
    }

    @Test
    void flush() {
        entityManager.create(Department.class);
        int expectedRows;
        int actualRows;
        String rowCountQuery = "SELECT count(*) from department";
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
             Statement stmt = con.createStatement())
        {
            ResultSet rs = stmt.executeQuery(rowCountQuery);
            rs.next();
            expectedRows = rs.getInt(1) + 2;

            entityManager.flush();

            rs = stmt.executeQuery(rowCountQuery);
            rs.next();
            actualRows = rs.getInt(1);

            Assertions.assertEquals(expectedRows, actualRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    EntityManager getEntityManager(){
        try(Connection con = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
            Statement stmt = con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE entity " +
                    "(id bigint primary key , number bigint, name varchar(50), age int, count decimal, localdatetime timestamp, " +
                    " date date, timestamp timestamp, localdate date, dept_id bigint); " +
                    "CREATE TABLE department (id bigint primary key , location varchar(50));");
        }catch (Exception e){
            throw new IllegalArgumentException("", e);
        }
        return new EntityManager("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "");
    }
}