package com.griddynamics.pift;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@UtilityClass
public class SQLUtils {

    private static final Set<Class<?>> exclusionAnnotationSet = Set.of(
            JoinColumn.class,
            Id.class,
            Version.class
    );

    /**
     * Returns the field value valid for the request.
     *
     * @param field  to be read.
     * @param target object to be read from.
     */
    public static String readField(Field field, Object target) {
        try {
            Object o = FieldUtils.readField(field, target, true);
            if (o instanceof String) {
                return "'" + o + "'";
            }
            return o.toString();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String createQueryForSelect(Object entity) {
        String selectQuery = "SELECT * FROM " +
                ReflectionUtils.getTableName(entity.getClass()) +
                " WHERE " + getQueryCondition(entity);
        log.debug(selectQuery);
        return selectQuery;
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

        ReflectionUtils.getColumnFields(type).forEach(field -> {
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

    /**
     * Gets the table column name that matches the received field.
     *
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

    public static String createQueryForSelectById(Class<?> type, Object id){
        return "SELECT * FROM " +
                ReflectionUtils.getTableName(type) +
                " WHERE " +
                getColumnName(getIdField(type)) +
                " = " + convertObjectToString(id);
    }

    private static String convertObjectToString(Object obj){
        if (obj instanceof String) {
            return "'" + obj + "'";
        }
        return obj.toString();
    }

    public static Field getIdField(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Exception in getIdField method"));
    }

    /**
     * Gets id field from object reference field.
     *
     * @param field that references another object.
     * @return id field
     */
    private static Field getIdField(Object entity, Field field) {
        return Arrays.stream(ReflectionUtils.getFieldValue(field, entity).getClass()
                        .getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Exception in getIdField method"));
    }

    private static String getQueryCondition(Object entity) {
        StringBuilder res = new StringBuilder();
        getNotNullColumnFields(entity).forEach(field -> {
            if (!res.isEmpty()) {
                res.append(" AND ");
            }
            res.append(field.getName());
            res.append(" = ").append(SQLUtils.readField(field, entity));
        });
        return res.toString();
    }

    private static List<Field> getNotNullColumnFields(Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> Collections.disjoint(exclusionAnnotationSet, Set.of(field.getAnnotations())))
                .filter(field -> ReflectionUtils.getFieldValue(field, entity) != null)
                .toList();
    }
}
