package com.griddynamics.uspanov.test;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
        String insertQuery = "insert into entity_test values (:name, :number, :age, :count)";
        Class<?> type = entity.getClass();
        Map<String, Object> entityMap = new HashMap<>();
        Arrays.stream(type.getDeclaredFields())
                .forEach(field -> entityMap.put(getColumnName(field), readField(field, entity)));
        try (Connection con = dataSource.open()) {
            con.createQuery(insertQuery)
                    .addParameter("name", (String) entityMap.get("name"))
                    .addParameter("number", (Long) entityMap.get("number"))
                    .addParameter("age", (Integer) entityMap.get("age"))
                    .addParameter("count", (BigDecimal) entityMap.get("count"))
                    .executeUpdate();
        }
    }

    private String getColumnName(Field field) {
        if (field.isAnnotationPresent(javax.persistence.Column.class)) {
            System.out.println(field.getAnnotation(Column.class).name());
            return field.getAnnotation(Column.class).name();
        }
        System.out.println(field.getName());
        return field.getName();
    }

    private Object readField(Field field, Object target) {
        try {
            return FieldUtils.readField(field, target, true);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
