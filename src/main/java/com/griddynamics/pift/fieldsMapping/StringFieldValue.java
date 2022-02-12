package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

public class StringFieldValue implements FieldValue<String>{
    private final Faker faker = new Faker();

    @Override
    public String generate() {
        return faker.animal().name();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getValueFromString(String value) {
        return value;
    }
}
