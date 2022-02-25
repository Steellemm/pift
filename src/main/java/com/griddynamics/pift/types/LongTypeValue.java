package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.NumberTypeValue;

@SuppressWarnings("unused")
public class LongTypeValue implements NumberTypeValue<Long> {

    @Override
    public Long generate(int value) {
        return (long) value;
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Long parse(String value) {
        return Long.parseLong(value);
    }
}
