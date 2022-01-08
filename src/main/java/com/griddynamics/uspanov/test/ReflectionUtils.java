package com.griddynamics.uspanov.test;


import lombok.experimental.UtilityClass;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@UtilityClass
public class ReflectionUtils {

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class) &&
                !field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }

    public static String getTableName(Class<?> type) {
        if (type.isAnnotationPresent(Table.class) &&
                !type.getAnnotation(Table.class).name().isBlank()) {
            return type.getAnnotation(Table.class).name();
        }
        return type.getSimpleName();
    }

    public static Stream<String> getColumnNameStream(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(x ->
                        !x.isAnnotationPresent(Transient.class) &&
                                !x.isAnnotationPresent(OneToOne.class))
                .map(ReflectionUtils::getColumnName);
    }

    public static boolean checkIfFieldFilled(Field field, Object object) {
        boolean accessStatus = field.canAccess(object);
        field.setAccessible(true);
        try {
            return field.get(object) == null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in checkIfFieldFilled method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
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
}
