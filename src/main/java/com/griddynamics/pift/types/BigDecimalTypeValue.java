package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.NumberTypeValue;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class BigDecimalTypeValue implements NumberTypeValue<BigDecimal> {

    @Override
    public BigDecimal generate(int value) {
        return new BigDecimal(value);
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal parse(String value) {
        return new BigDecimal(value);
    }
}
