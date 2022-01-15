package com.griddynamics.pift;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.util.Arrays;

@Slf4j
public class SQLUtils {

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
                        readField
                                (getIdField(entity, field), ReflectionUtils.getFieldValue(field, entity))
                );
            } else values.append(readField(field, entity));
            insertQuery.append(getColumnName(field));
        });
        return insertQuery.append(") values (").append(values).append(")").toString();
    }

    private static Field getIdField(Object entity, Field field) {
        return Arrays.stream(ReflectionUtils.getFieldValue(field, entity).getClass()
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
