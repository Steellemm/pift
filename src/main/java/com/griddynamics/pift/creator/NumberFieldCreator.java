package com.griddynamics.pift.creator;

import com.github.javafaker.Faker;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

public class NumberFieldCreator implements FieldCreator{
    private final Faker faker = new Faker();

    private final Map<Class<?>, Function<Column, Object>> fieldsMapping = Map.of(
            BigDecimal.class, column -> {
                if (column.getCondition() != null) {
                    return new BigDecimal(faker.number().numberBetween
                            (Long.parseLong(column.getCondition().getMin()), Long.parseLong(column.getCondition().getMax())));
                } return new BigDecimal(faker.number().randomNumber());
            },
            Long.class, column -> {
                if (column.getCondition() != null) {
                    return faker.number().numberBetween
                            (Long.parseLong(column.getCondition().getMin()), Long.parseLong(column.getCondition().getMax()));
                } return faker.number().randomNumber();
            },
            Integer.class, column -> {
                if (column.getCondition() != null) {
                    return faker.number().numberBetween
                            (Integer.parseInt(column.getCondition().getMin()), Integer.parseInt(column.getCondition().getMax()));
                } return faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
    );

    @Override
    public Object createValue(Field field, Column column) {
        return fieldsMapping.get(field.getType()).apply(column);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.NUMBER;
    }
}
