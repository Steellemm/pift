package com.griddynamics.uspanov.test;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
        Class<?> type = entity.getClass();
        String tableName;
        System.out.println(type.getAnnotation(Table.class).name());
        if (type.isAnnotationPresent(Table.class) && !type.getAnnotation(Table.class).name().equals("")) {
            tableName = type.getAnnotation(Table.class).name();
        } else tableName = type.getSimpleName();

        StringBuilder insertQuery = new StringBuilder("insert into " + tableName + " (" + Arrays
                .stream(type.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.joining(",")) + ") values (");

        Arrays.stream(type.getDeclaredFields())
                .filter(x -> !x.isAnnotationPresent(Transient.class))
                .forEach(x -> insertQuery.append(":").append(x.getName()).append(", "));

        insertQuery.setCharAt(insertQuery.lastIndexOf(","), ')');

        try (Connection con = dataSource.open()) {
            con.createQuery(insertQuery.toString(), true)
                    .bind(entity)
                    .executeUpdate();
        }
    }
}
