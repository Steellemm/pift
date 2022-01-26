package com.griddynamics.pift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.pojo.ColumnProps;
import com.griddynamics.pift.pojo.Pojo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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
        createdEntitiesList.clear();
    }

    /**
     * Creates new instance of type class with random values in fields.
     *
     * @param type of the entity class.
     * @return new instance of type class.
     */
    public <T> T create(Class<T> type) {
        return create(type, type.getSimpleName());
    }

    public <T> T create(Class<T> type, String entityId) {
        log.debug(createdEntitiesMap.keySet().toString());
        T object = EntityUtils.create(type, createdEntitiesList);
        createdEntitiesMap.put(getEntityClassName(entityId), object);
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

    private String getEntityClassName(String entityClassName) {
        String str = entityClassName;
        int counter = 0;
        while (createdEntitiesMap.containsKey(entityClassName)) {
            entityClassName = str + (++counter);
        }
        return entityClassName;
    }

}
