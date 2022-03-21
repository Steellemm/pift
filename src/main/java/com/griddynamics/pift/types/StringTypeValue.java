package com.griddynamics.pift.types;

import com.github.javafaker.Faker;
import com.griddynamics.pift.creator.TypeValue;
import com.griddynamics.pift.model.Condition;
import nl.flotsam.xeger.Xeger;

@SuppressWarnings("unused")
public class StringTypeValue implements TypeValue<String> {
    private final Faker faker = new Faker();

    @Override
    public String generate() {
        return faker.regexify("[A-Za-z0-9]{20}");
    }

    @Override
    public String generate(Condition condition) {
        if (condition.getFormat() == null || condition.getFormat().isEmpty()) {
            return generate();
        }
        try {
            return new Xeger(condition.getFormat()).generate();
        } catch (Exception e) {
            throw new IllegalStateException("Regular expression in wrong " + condition.getFormat());
        }
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String parse(String value) {
        return value;
    }

    @Override
    public String toString(Object value) {
        return "'" + value.toString() + "'";
    }
}
