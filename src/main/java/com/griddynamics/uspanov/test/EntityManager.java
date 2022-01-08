package com.griddynamics.uspanov.test;

import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.griddynamics.uspanov.test.EntityUtils.*;
import static com.griddynamics.uspanov.test.ReflectionUtils.getTableName;


@Slf4j
public class EntityManager {
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private static final Sql2o dataSource = new Sql2o
            ("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");

    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, createdEntitiesList);
    }

    private void saveEntity(Object entity) {
        Class<?> type = entity.getClass();
        String tableName = getTableName(type);
        String insertQuery = createInsertQuery(tableName, type);

        try (Connection con = dataSource.open()) {
            log.debug(insertQuery);
            con.setRollbackOnException(false);
            con.createQuery(insertQuery).bind(entity).executeUpdate();
        }
    }

}
