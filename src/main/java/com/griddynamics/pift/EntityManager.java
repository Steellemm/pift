package com.griddynamics.pift;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final Map<String, Object> createdEntitiesMap = new HashMap<>();
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    /**
     * Pushes the objects from createdEntitiesList into database and then clears the list.
     */
    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        log.debug(createdEntitiesMap.toString());
        createdEntitiesList.clear();
    }

    /**
     * Creates new instance of type class with random values in fields.
     *
     * @param type of the entity class.
     * @return new instance of type class.
     */
    public <T> T create(Class<T> type) {
        T object = EntityUtils.create(type, createdEntitiesList);
        createdEntitiesMap.put(EntityUtils
                .getEntityClassName(object.getClass().getSimpleName(), createdEntitiesMap), object);
        return object;
    }

    public <T> T create(Class<T> type, String entityId) {
        T object = EntityUtils.create(type, createdEntitiesList);
        createdEntitiesMap.put(EntityUtils
                .getEntityClassName(entityId, createdEntitiesMap), object);
        return object;
    }

    /**
     * Saves received object to the database.
     */
    private void saveEntity(Object entity) {
        executeQuery(SQLUtils.createQueryForInsert(entity));
    }


    private void executeQuery(String query) {
        log.debug(query);
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

}
