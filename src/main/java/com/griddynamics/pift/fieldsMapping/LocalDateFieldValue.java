package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDateFieldValue implements FieldValue<LocalDate> {
    private final Faker faker = new Faker();

    @Override
    public LocalDate generate() {
        return faker.date().birthday().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate();
    }

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }

    @Override
    public LocalDate getValueFromString(String value) {
        return LocalDate.parse(value);
    }
}
