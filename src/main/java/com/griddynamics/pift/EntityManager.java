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

    /***
     * Pushes the objects from createdEntitiesList into database and then clears the list.
     * @return int number of affected rows in database.
     */
    public int flush() {
        AtomicInteger rowsAffectedCount = new AtomicInteger(0);
        createdEntitiesList.forEach(x -> rowsAffectedCount.addAndGet(saveEntity(x)));
        log.debug(String.valueOf(rowsAffectedCount.get()));
        createdEntitiesList.clear();
        return rowsAffectedCount.get();
    }

    /***
     * Creates new instance of type class with random values in fields.
     * @param type of the entity class.
     * @return new instance of type class.
     */
    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, createdEntitiesList);
    }

    /***
     * Saves received object to the database.
     * @param entity object
     * @return int number of affected rows in database.
     */
    private int saveEntity(Object entity) {
        return executeQuery(SQLUtils.createQueryForInsert(entity));
    }

    /***
     * Executes a query to the database.
     * @param query
     * @return int number of affected rows in database.
     */
    private int executeQuery(String query){
            log.debug(query);
            try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = con.createStatement()) {
                return stmt.executeUpdate(query);
            } catch (Exception e) {
                throw new IllegalArgumentException("Exception in connect method", e);
            }
    }

}
