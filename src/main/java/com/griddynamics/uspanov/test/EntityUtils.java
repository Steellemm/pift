package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;


@UtilityClass
@Slf4j
public class EntityUtils {
    private final Faker faker = new Faker();

    private final Map<Class, Function<Field, Object>> map = Map.of(
            Long.class, field -> faker.number().randomNumber(),
            String.class, field -> faker.animal().name(),
            Integer.class, field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            BigDecimal.class, field -> new BigDecimal(faker.number().randomNumber())
    );

    public static <T> T create(Class<T> type){
        T object = createInstance(type);
        try {
            Arrays.stream(type.getDeclaredFields()).forEach(field -> setField(object, field));
        } catch (Exception e) {
            log.error("Exception in create method", e);
        }
        return object;
    }

    private static <T> T createInstance(Class<T> type){
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }

    private static void setField(Object obj, Field field){
        boolean a = field.canAccess(obj);
        try {
            field.setAccessible(true);
            field.set(obj, map.get(field.getType()).apply(field));
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(a);
        }
    }
}
