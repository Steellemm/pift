package com.griddynamics.pift.fieldsMapping;

import com.github.javafaker.Faker;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class SqlDateFieldValue implements FieldValue<Date>{
    private final Faker faker = new Faker();

    @Override
    public Date generate() {
        return  new Date(faker.date().birthday().getTime());
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }

    @Override
    public Date getValueFromString(String value) {
        try {
            return new Date(new SimpleDateFormat("MM-dd-yyyy").parse(value).getTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in getValueFromString method");
        }
    }
}
