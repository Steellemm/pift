package com.griddynamics.pift.types;

import com.griddynamics.pift.model.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;


public class DoubleTypeValueTest {

    DoubleTypeValue typeValue = new DoubleTypeValue();

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

        Double minValue = min == null ? -Double.MAX_VALUE : Double.parseDouble(min);
        Double maxValue = max == null ? Double.MAX_VALUE : Double.parseDouble(max);
        Double value = typeValue.generate(condition);
        assertTrue(value.compareTo(minValue) >= 0, "Value: " + value);
        assertTrue(value.compareTo(maxValue) <= 0, "Value: " + value);
    }

    @Test
    void getType() {
        assertEquals(typeValue.getType(), Double.class);
    }

    @Test
    void parse() {
        Double value = typeValue.parse("123.45");
        assertEquals(Double.parseDouble("123.45"), value);
    }
}
