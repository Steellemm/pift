package com.griddynamics.pift.utils;


import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reflections.Reflections;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.reflections.scanners.Scanners.SubTypes;

@UtilityClass
public class ReflectionUtils {

    /**
     * Checks if received field is filled in the object.
     *
     * @param field  to be checked.
     * @param object target.
     * @return boolean
     */
    public static boolean checkIfFieldFilled(Field field, Object object) {
        return getFieldValue(field, object) != null;
    }

    /**
     * Gets the table name of received class.
     *
     * @param type object.
     * @return String name of table.
     */
    public static String getTableName(Class<?> type) {
        if (type.isAnnotationPresent(Table.class) &&
                !type.getAnnotation(Table.class).name().isEmpty()) {
            return type.getAnnotation(Table.class).name();
        }
        return type.getSimpleName();
    }

    public static String getTableName(Field field) {
        return getTableName(field.getDeclaringClass());
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
                !field.getAnnotation(Column.class).name().isEmpty()) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }

    /**
     * Gets object fields that need to be matched with table columns.
     *
     * @return Stream of fields.
     */
    public static Stream<Field> getColumnFields(Class<?> type) {
        Stream<Field> stream = Arrays.stream(type.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(Version.class));
        if (type.getSuperclass() != Object.class) {
            return Stream.concat(stream, getColumnFields(type.getSuperclass()));
        } else {
            return stream;
        }
    }


    /**
     * @param field  to be got.
     * @param object to be read from.
     * @return Object field value.
     */
    public static Object getFieldValue(Field field, Object object) {
        try {
            return FieldUtils.readField(object, field.getName(), true);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        }
    }

    /**
     * Sets value in field.
     *
     * @param obj   object which field to be set.
     * @param field to be set.
     * @param value to be set in field.
     */
    public static void setFieldValue(Object obj, Field field, Object value) {
        try {
            FieldUtils.writeField(field, obj, value, true);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        }
    }

    /**
     * Creates new instance of received class.
     *
     * @param type object.
     * @return new instance of received class.
     */
    public static <T> T createInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }

    public static <T> Object getEntityWithId(Class<T> type, Object id) {
        Object obj = ReflectionUtils.createInstance(type);
        ReflectionUtils.setFieldValue(obj, getIdField(type), id);
        return obj;
    }

    public static void checkOnTable(Class<?> type) {
        if (!type.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("POJO is not reflection of table: " + type.getCanonicalName());
        }
    }

    public static void setIdField(Object entity, Object id) {
        Field idField = getIdField(entity.getClass());
        setFieldValue(entity, idField, id);
    }

    public static Field getIdField(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Exception in getIdField method"));
    }

    public static Object getIdValue(Object entity) {
        return getFieldValue(getIdField(entity.getClass()), entity);
    }

    public static <T> Stream<T> getClassHeirs(Class<T> parent, String packet) {
        return new Reflections(packet).get(SubTypes.of(parent).asClass())
                .stream()
                .filter(c -> !c.isInterface())
                .map(heir -> (T) createInstance(heir));
    }
}
