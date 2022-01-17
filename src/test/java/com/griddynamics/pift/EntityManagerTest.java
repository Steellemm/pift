package com.griddynamics.pift;

import com.griddynamics.pift.Entities.Department;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerTest {
    EntityManager entityManager = new EntityManager
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
    String rowCountQuery = "SELECT count(*) from department";

    @Test
    void create() {
        Assertions.assertNotNull(entityManager.create(Department.class));
    }

    @Test
    void flush() {
        entityManager.create(Department.class);
        int expectedRows;
        int actualRows;
        try (Connection con = DriverManager.getConnection
                ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(rowCountQuery);
            rs.next();
            expectedRows = rs.getInt(1) + 1;

            entityManager.flush();

            rs = stmt.executeQuery(rowCountQuery);
            rs.next();
            actualRows = rs.getInt(1);

            Assertions.assertEquals(expectedRows, actualRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}