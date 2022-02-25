package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.NumberTypeValue;

@SuppressWarnings("unused")
public class IntegerTypeValue implements NumberTypeValue<Integer> {

    @Override
    public Integer generate(int value) {
        return value;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }
}
