package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.BiConsumer;

public class EntityManager<T> {
    private final Faker faker = new Faker();
    private final Map<String, BiConsumer<Field, T>> map = Map.of("Long", (field, obj) -> {
        try {
            field.set(obj, faker.number().randomNumber());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    });


    T create(Class<T> type) throws NoSuchMethodException,
                                    InvocationTargetException, InstantiationException, IllegalAccessException {
        T object = type.getConstructor().newInstance();
        for (Field field: type.getDeclaredFields()){
            map.get(field.getType().getName()).accept(field, object);
        }
        return object;
    }
}
