package com.griddynamics.pift;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, createdEntitiesList);
    }

    private void saveEntity(Object entity) {
        executeInsertQuery(entity);
    }

    private void executeInsertQuery(Object entity) {
        String insertQuery = SQLUtils.createQueryForInsert(entity);
        log.debug(insertQuery);
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(insertQuery);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

}
