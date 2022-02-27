package com.griddynamics.pift.utils;

import com.griddynamics.pift.model.PiftProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void getPropertiesTest() {
        assertDoesNotThrow(JsonUtils::getProperties);
        PiftProperties properties = JsonUtils.getProperties();
        assertNotNull(properties);
    }
}