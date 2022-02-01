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

import static org.junit.jupiter.api.Assertions.*;

class StringFieldCreatorTest {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    PiftProperties piftProperties;
    StringFieldCreator stringFieldCreator = new StringFieldCreator();

    @Test
    void createValue() {
        try {
            piftProperties = mapper.readValue(new File("src/test/resources/pift.yaml"), PiftProperties.class);
            Column column = piftProperties.getTables().get("entity").getColumns().get("name");
            String name = (String) stringFieldCreator.createValue(Entity.class.getDeclaredField("name"), column);
            Assertions.assertFalse(name.isEmpty());
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createValue test method", e);
        }
    }

    @Test
    void getFieldType() {
        Assertions.assertEquals(FieldType.STRING, stringFieldCreator.getFieldType());
    }
}