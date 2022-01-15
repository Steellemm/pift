package com.griddynamics.pift;


import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

@UtilityClass
public class ReflectionUtils {

    public static boolean checkIfFieldFilled(Field field, Object object) {
        return getFieldValue(field, object) == null;
    }

    public static String getTableName(Class<?> type) {
        if (type.isAnnotationPresent(Table.class) &&
                !type.getAnnotation(Table.class).name().isBlank()) {
            return type.getAnnotation(Table.class).name();
        }
        return type.getSimpleName();
    }

    public static Stream<Field> getColumnFields(Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(Version.class));
    }

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

    public static void setFieldValue(Object obj, Field field, Object value) {
        boolean accessStatus = setAccess(field, obj);
        try {
            field.set(obj, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
    }

    private static boolean setAccess(Field field, Object obj){
        boolean accessStatus = field.canAccess(obj);
        field.setAccessible(true);
        return accessStatus;
    }

    public static <T> T createInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }
}
