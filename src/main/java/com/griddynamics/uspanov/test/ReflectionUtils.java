package com.griddynamics.uspanov.test;


import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtils {

    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(javax.persistence.Column.class)) {
            System.out.println(field.getAnnotation(Column.class).name());
            return field.getAnnotation(Column.class).name();
        }
        System.out.println(field.getName());
        return field.getName();
    }

    public static Object readField(Field field, Object target) {
        try {
            return FieldUtils.readField(field, target, true);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
