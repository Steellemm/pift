package com.griddynamics.pift.creator;

import com.griddynamics.pift.model.Condition;

public interface TypeValue<T> {

    T generate();

    default T generate(Condition condition) {
        return generate();
    }

    Class<T> getType();

    T parse(String value);

    default String toString(Object value) {
        return value.toString();
    }
}
