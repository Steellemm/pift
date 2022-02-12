package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

import java.math.BigDecimal;

public class BigDecimalFieldValue implements FieldValue<BigDecimal> {
    private final Faker faker = new Faker();


    @Override
    public BigDecimal generate() {
        return new BigDecimal(faker.number().randomNumber());
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal getValueFromString(String value) {
        return new BigDecimal(value);
    }
}
