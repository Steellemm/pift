package com.griddynamics.pift.utils;

import com.griddynamics.pift.FieldCreatorManager;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class SQLUtils {

    private static final Set<Class<?>> exclusionAnnotationSet = new HashSet<>(
            Arrays.asList(JoinColumn.class, Id.class, Version.class)
    );

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Returns the field value valid for the request.
     *
     * @param field  to be read.
     * @param target object to be read from.
     */
    public static String readField(Field field, Object target) {
        try {
            Object value = FieldUtils.readField(field, target, true);
            if (value.getClass().equals(Date.class)) {
                return "'" + dateFormat.format((Date) value) + "'";
            }
            if (value instanceof String || value instanceof Date || value instanceof Temporal) {
                return "'" + value + "'";
            }
            return value.toString();
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
    public static String createQueryForInsert(Object entity, FieldCreatorManager fieldCreatorManager) {
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
                values.append(readField(getIdField(entity, field), ReflectionUtils.getFieldValue(field, entity)));
            } else {
                values.append(readField(field, entity));
            }
            insertQuery.append(ReflectionUtils.getColumnName(field));
        });
        return insertQuery.append(") values (").append(values).append(")").toString();
    }

    public String createQueryForSelectById(Class<?> type, Object id){
        return "SELECT * FROM " +
                    ReflectionUtils.getTableName(type) +
                    " WHERE " +
                    ReflectionUtils.getColumnName(ReflectionUtils.getIdField(type)) +
                    " = " + convertObjectToString(id);
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

    private static String convertObjectToString(Object obj){
        if (obj instanceof String) {
            return "'" + obj + "'";
        }
        return obj.toString();
    }

    private static String getQueryCondition(Object entity) {
        StringBuilder res = new StringBuilder();
        getNotNullColumnFields(entity).forEach(field -> {
            if (res.length() != 0) {
                res.append(" AND ");
            }
            res.append(field.getName());
            res.append(" = ").append(SQLUtils.readField(field, entity));
        });
        return res.toString();
    }

    private static List<Field> getNotNullColumnFields(Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> Collections.disjoint(exclusionAnnotationSet, new HashSet<>(Arrays.asList(field.getAnnotations()))))
                .filter(field -> ReflectionUtils.getFieldValue(field, entity) != null)
                .collect(Collectors.toList());
    }


}
