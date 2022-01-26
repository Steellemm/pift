package com.griddynamics.pift;


import lombok.experimental.UtilityClass;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

@UtilityClass
public class ReflectionUtils {

    /**
     * Checks if received field is filled in the object.
     * @param field to be checked.
     * @param object target.
     * @return boolean
     */
    public static boolean checkIfFieldFilled(Field field, Object object) {
        return getFieldValue(field, object) == null;
    }

    /**
     * Gets the table name of received class.
     * @param type object.
     * @return String name of table.
     */
    public static String getTableName(Class<?> type) {
        if (type.isAnnotationPresent(Table.class) &&
                !type.getAnnotation(Table.class).name().isBlank()) {
            return type.getAnnotation(Table.class).name();
        }
        return type.getSimpleName();
    }


    /**
     * Gets object fields that need to be matched with table columns.
     * @return Stream of fields.
     */
    public static Stream<Field> getColumnFields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(Version.class));
    }


    /**
     * @param field to be got.
     * @param object to be read from.
     * @return Object field value.
     */
    public static Object getFieldValue(Field field, Object object){
        boolean accessStatus = field.canAccess(object);
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
    }


    /**
     * Sets value in field.
     * @param obj object which field to be set.
     * @param field to be set.
     * @param value to be set in field.
     */
    public static void setFieldValue(Object obj, Field field, Object value) {
        boolean accessStatus = field.canAccess(obj);
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
    }


    /**
     * Creates new instance of received class.
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
}
