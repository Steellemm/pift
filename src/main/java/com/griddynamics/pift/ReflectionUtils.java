package com.griddynamics.pift;


import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class ReflectionUtils {

    private final static Map<Class<?>, Function<Long, Object>> longToDateConverterMap = Stream.of(new Object[][]{
                    {LocalDate.class, (Function<Long, Object>) LocalDate::ofEpochDay},
                    {LocalDateTime.class, (Function<Long, Object>) epochSecond -> LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)},
            }
    ).collect(Collectors.toMap(data -> (Class<?>) data[0], data -> (Function<Long, Object>) data[1]));

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
                !type.getAnnotation(Table.class).name().isEmpty()) {
            return type.getAnnotation(Table.class).name();
        }
        return type.getSimpleName();
    }

    public static String getTableName(Field field) {
        return getTableName(field.getDeclaringClass());
    }


    /**
     * Gets object fields that need to be matched with table columns.
     * @param entity object.
     * @return Stream of fields.
     */
    public static Stream<Field> getColumnFields(Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(Version.class));
    }

    /**
     * @param field to be got.
     * @param object to be read from.
     * @return Object field value.
     */
    public static Object getFieldValue(Field field, Object object){
        try {
            return FieldUtils.readField(object, field.getName(), true);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        }
    }


    /**
     * Sets value in field.
     * @param obj object which field to be set.
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

    public static <T> T createInstance(Class<T> type, Long msec) {
        try {
            if (longToDateConverterMap.containsKey(type)){
                return (T) longToDateConverterMap.get(type).apply(msec);
            }
            return type.getConstructor(Long.TYPE).newInstance(msec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }

}
