package com.griddynamics.pift;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final String url;
    private final String user;
    private final String password;

    /**
     * Pushes the objects from createdEntitiesList into database and then clears the list.
     */
    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    public <T> List<T> getList(T entity) {
        Class<T> type = (Class<T>) entity.getClass();
        EntityUtils.checkOnTable(type);
        String queryForSelect = SQLUtils.createQueryForSelect(entity);
        List<T> entityList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(queryForSelect)
        ) {
            while (rs.next()) {
                entityList.add(EntityUtils.getEntityFromResultSet(type, rs));
            }
            return entityList;
        } catch (Exception e) {
            throw new IllegalStateException("Exception in getList method", e);
        }
    }

    public <T> Optional<T> getById(Class<T> type, Object id) {
        EntityUtils.checkOnTable(type);
        String query = SQLUtils.createQueryForSelectById(type, id);
        log.debug(query);
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)
        ) {
            if (rs.isBeforeFirst()) {
                rs.next();
                return Optional.of(EntityUtils.getEntityFromResultSet(type, rs));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception in getById method", e);
        }
    }

    /**
     * Creates new instance of type class with random values in fields.
     *
     * @param type of the entity class.
     * @return new instance of type class.
     */
    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, createdEntitiesList);
    }

    /**
     * Saves received object to the database.
     */
    private void saveEntity(Object entity) {
        executeQuery(SQLUtils.createQueryForInsert(entity));
    }

    private void executeQuery(String query) {
        log.debug(query);
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

}
