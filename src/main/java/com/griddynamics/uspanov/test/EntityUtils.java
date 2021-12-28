package com.griddynamics.uspanov.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;

public class EntityUtils {
    private EntityUtils() {
    }

    public static <T> T create(Class<T> type, Map<String, Function<Field, Object>> map){
        T object = null;
        try {
            object = type.getConstructor().newInstance();
            for (Field field: type.getDeclaredFields()){
                field.setAccessible(true);
                field.set(object, map.get(field.getType().getName().split("\\.")[2]).apply(field));
                field.setAccessible(false);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return object;
    }
}
