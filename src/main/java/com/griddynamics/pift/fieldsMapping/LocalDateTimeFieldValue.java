package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeFieldValue implements FieldValue<LocalDateTime> {
    private final Faker faker = new Faker();

    @Override
    public LocalDateTime generate() {
        return LocalDateTime.ofInstant(faker.date().birthday().toInstant(), ZoneId.of("Europe/London"));
    }

    @Override
    public Class<LocalDateTime> getType() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime getValueFromString(String value) {
        return LocalDateTime.parse(value);
    }
}
