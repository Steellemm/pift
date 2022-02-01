package com.griddynamics.pift.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.Entities.Entity;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import com.griddynamics.pift.model.PiftProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NumberFieldCreatorTest {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    PiftProperties piftProperties;
    NumberFieldCreator numberFieldCreator = new NumberFieldCreator();

    @Test
    void createValue() {
        try {
            piftProperties = mapper.readValue(new File("src/test/resources/pift.yaml"), PiftProperties.class);
            Column column = piftProperties.getTables().get("entity").getColumns().get("age");
            Integer number = (Integer) numberFieldCreator.createValue(Entity.class.getDeclaredField("age"), column);
            Assertions.assertTrue(number >= Integer.parseInt(column.getCondition().getMin()) && number < Integer.parseInt(column.getCondition().getMax()));
        } catch (Exception e) {
           throw new IllegalArgumentException("Exception in createValue test method", e);
        }
    }

    @Test
    void getFieldType() {
        Assertions.assertEquals(FieldType.NUMBER, numberFieldCreator.getFieldType());
    }
}