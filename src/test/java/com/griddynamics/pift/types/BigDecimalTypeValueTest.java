package com.griddynamics.pift.types;

import com.griddynamics.pift.model.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalTypeValueTest {

    BigDecimalTypeValue typeValue = new BigDecimalTypeValue();

    @Test
    void generate() {
        assertDoesNotThrow(() -> typeValue.generate());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1,5",
            "1,null",
            "null,10"},
            nullValues={"null"})
    void generateCondition(String min, String max) {
        Condition condition = new Condition();
        condition.setMin(min);
        condition.setMax(max);
        BigDecimal minValue = min == null ? BigDecimal.valueOf(Integer.MIN_VALUE) : new BigDecimal(min);
        BigDecimal maxValue = max == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : new BigDecimal(max);;
        BigDecimal value = typeValue.generate(condition);
        assertTrue(value.compareTo(minValue) >= 0, "Value: " + value.toPlainString());
        assertTrue(value.compareTo(maxValue) <= 0, "Value: " + value.toPlainString());
    }

    @Test
    void getType() {
        assertEquals(typeValue.getType(), BigDecimal.class);
    }

    @Test
    void parse() {
        BigDecimal value = typeValue.parse("123.45");
        assertEquals(new BigDecimal("123.45"), value);
    }
}