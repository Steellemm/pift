package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

public class IntegerFieldValue implements FieldValue<Integer>{
    private final Faker faker = new Faker();

    @Override
    public Integer generate() {
        return faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer getValueFromString(String value) {
        return Integer.parseInt(value);
    }
}
