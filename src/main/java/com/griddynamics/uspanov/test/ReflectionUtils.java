package com.griddynamics.uspanov.test;


import lombok.experimental.UtilityClass;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtils {

    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class) &&
                !field.getAnnotation(Column.class).name().equals("")) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }

    public static String getTableName(Class<?> type) {
        if (type.isAnnotationPresent(Table.class) && !type.getAnnotation(Table.class).name().equals("")) {
            return  type.getAnnotation(Table.class).name();
        } else return type.getSimpleName();
    }
}
