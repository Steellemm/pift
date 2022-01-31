package com.griddynamics.pift;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class SQLUtils {

    /**
     * Returns the field value valid for the request.
     * @param field to be read.
     * @param target object to be read from.
     */
    public static String readField(Field field, Object target) {
        try {
            Object o = FieldUtils.readField(field, target, true);
            if (o instanceof String || o instanceof Date || o instanceof Temporal) {
                return "'" + o + "'";
            }
            return o.toString();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates query for insert data into database.
     */
    public static String createQueryForInsert(Object entity) {
        Class<?> type = entity.getClass();
        StringBuilder insertQuery =
                new StringBuilder("INSERT INTO ")
                        .append(ReflectionUtils.getTableName(type)).append(" (");
        StringBuilder values = new StringBuilder();

        ReflectionUtils.getColumnFields(entity).forEach(field -> {
            if (values.length() > 0) {
                values.append(",").append(" ");
                insertQuery.append(",").append(" ");
            }
            if (field.isAnnotationPresent(JoinColumn.class)) {
                values.append(
                        readField(getIdField(entity, field), ReflectionUtils.getFieldValue(field, entity))
                );
            } else values.append(readField(field, entity));
            insertQuery.append(getColumnName(field));
        });
        return insertQuery.append(") values (").append(values).append(")").toString();
    }

    public static Field getIdField(Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Exception in getIdField method"));
    }

    /**
     * Gets id field from object reference field.
     * @param field that references another object.
     * @return id field
     */
    public static Field getIdField(Object entity, Field field) {
        return Arrays.stream(ReflectionUtils.getFieldValue(field, entity).getClass()
                        .getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Exception in getIdField method"));
    }

    /**
     * Gets the table column name that matches the received field.
     * @param field to be matched.
     * @return String name of column.
     */
    public static String getColumnName(Field field) {
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
