package com.griddynamics.pift.creator;

import com.github.javafaker.Faker;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import nl.flotsam.xeger.Xeger;

import java.lang.reflect.Field;

public class StringFieldCreator implements FieldCreator {

    private static final Faker faker = new Faker();

    @Override
    public Object createValue(Field field, Column column) {
        Xeger generator = new Xeger(column.getFormat());
        if (column.getFormat() == null || column.getFormat().isBlank()) {
            throw new IllegalArgumentException("format is not valid " + column.getFormat());
        }
        return generator.generate();
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.STRING;
    }
}
