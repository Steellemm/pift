package com.griddynamics.pift.creator;

import com.github.javafaker.Faker;
import com.griddynamics.pift.model.Condition;

public interface NumberTypeValue<T extends Number> extends TypeValue<T> {


    @Override
    default T generate() {
        return generate(new Faker().number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE));
    }

    @Override
    default T generate(Condition condition) {
        int min = condition.getMin() == null ? Integer.MIN_VALUE : Integer.parseInt(condition.getMin());
        int max = condition.getMax() == null ? Integer.MAX_VALUE : Integer.parseInt(condition.getMax());
        return generate(new Faker().number().numberBetween(min, max));
    }

    T generate(int value);
}
