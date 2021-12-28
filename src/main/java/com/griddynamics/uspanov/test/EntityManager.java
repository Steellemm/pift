package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;

public class EntityManager {
    private final Faker faker = new Faker();
    private final Map<String, Function<Field, Object>> map = Map.of("Long", field -> faker.number().randomNumber(),
            "String", field -> faker.animal().name());

    public <T> T create(Class<T> type) throws NoSuchMethodException,
                                    InvocationTargetException, InstantiationException, IllegalAccessException {
        T obj = type.getConstructor().newInstance();
        for (Field field: type.getDeclaredFields()){
            field.setAccessible(true);
            field.set(obj, map.get(field.getType().getName().split("\\.")[2]).apply(field));
            field.setAccessible(false);
        }
        return obj;
    }
}
