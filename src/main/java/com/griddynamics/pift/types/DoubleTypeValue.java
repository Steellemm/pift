package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.NumberTypeValue;

@SuppressWarnings("unused")
public class DoubleTypeValue implements NumberTypeValue<Double> {

    @Override
    public Double generate(int value) {
        return (double) value;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public Double parse(String value) {
        return Double.parseDouble(value);
    }
}