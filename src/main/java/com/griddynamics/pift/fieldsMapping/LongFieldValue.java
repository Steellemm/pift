package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

public class LongFieldValue implements FieldValue<Long> {
    private final Faker faker = new Faker();


    @Override
    public Long generate() {
        return faker.number().randomNumber();
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Long getValueFromString(String value) {
        return Long.parseLong(value);
    }
}
