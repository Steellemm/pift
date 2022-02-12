package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

import java.sql.Timestamp;

public class TimestampFieldValue implements FieldValue<Timestamp> {
    private final Faker faker = new Faker();

    @Override
    public Timestamp generate() {
        return Timestamp.from(faker.date().birthday().toInstant());
    }

    @Override
    public Class<Timestamp> getType() {
        return Timestamp.class;
    }

    @Override
    public Timestamp getValueFromString(String value) {
        return Timestamp.valueOf(value);
    }
}
