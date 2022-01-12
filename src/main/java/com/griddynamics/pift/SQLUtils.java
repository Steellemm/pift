package com.griddynamics.pift;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;

import static com.griddynamics.pift.ReflectionUtils.*;

@Slf4j
public class SQLUtils {
    public static void connect(String url, String user, String password, Object entity) {
        String insertQuery = createQueryForInsert(entity);
        log.debug(insertQuery);
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(insertQuery);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

    private static String createQueryForInsert(Object entity) {
        Class<?> type = entity.getClass();
        StringBuilder insertQuery =
                new StringBuilder("INSERT INTO ")
                        .append(getTableName(type)).append(" (");
        StringBuilder values = new StringBuilder();

        getColumnFields(entity).forEach(field -> {
            if (values.length() > 0) {
                values.append(",").append(" ");
                insertQuery.append(",").append(" ");
            }
            if (field.isAnnotationPresent(JoinColumn.class)) {
                values.append(
                        readField(getIdField(entity, field), getFieldValue(field, entity))
                );
            } else values.append(readField(field, entity));
            insertQuery.append(getColumnName(field));
        });
        return insertQuery.append(") values (").append(values).append(")").toString();
    }

    private static Field getIdField(Object entity, Field field) {
        return Arrays.stream(getFieldValue(field, entity).getClass()
                        .getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Exception in getIdField method"));
    }

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            return field.getAnnotation(JoinColumn.class).name();
        }
        if (field.isAnnotationPresent(Column.class) &&
                !field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }
}
