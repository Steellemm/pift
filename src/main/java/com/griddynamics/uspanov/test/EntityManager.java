package com.griddynamics.uspanov.test;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.persistence.Table;
import java.util.*;

public class EntityManager {
    private static final Sql2o dataSource = new Sql2o
            ("jdbc:postgresql://localhost:5432/test_db", "postgres", "postgres");
    private final List<Object> entityList = new ArrayList<>();

    public <T> T create(Class<T> type) {
        T obj = EntityUtils.create(type);
        entityList.add(obj);
        return obj;
    }

    public void flush() {
        entityList.forEach(this::saveEntity);
        entityList.clear();
    }

    private void saveEntity(Object entity) {
        String tableName = entity.getClass().getAnnotation(Table.class).name();
        StringBuilder insertQuery = new StringBuilder("insert into " + tableName + " values (");

        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(x -> {
            insertQuery.append(":").append(x.getName()).append(", ");
        });

        insertQuery.setCharAt(insertQuery.lastIndexOf(","), ')');

        try (Connection con = dataSource.open()) {
            con.createQuery(String.valueOf(insertQuery), true)
                    .bind(entity)
                    .executeUpdate();
        }
    }
}
