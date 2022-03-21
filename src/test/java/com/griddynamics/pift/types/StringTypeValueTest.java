package com.griddynamics.pift.types;

import com.griddynamics.pift.model.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringTypeValueTest {

    @Test
    void generate() {
        StringTypeValue stringTypeValue = new StringTypeValue();
        Condition condition = new Condition();
        condition.setFormat("[0-9]{4}_[0-9]{4}_[0-9]{4}_[A-Z]+");
        String generate = stringTypeValue.generate(condition);
        System.out.println(generate);

    }
}