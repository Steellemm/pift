package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

public class EntityManager {
    private final Faker faker = new Faker();
    private final Map<String, Function<Field, Object>> map = Map.of("Long", field -> faker.number().randomNumber(),
            "String", field -> faker.animal().name(),
            "Integer", field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            "BigDecimal", field -> new BigDecimal(faker.number().randomNumber()));

    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, map);
    }
}
