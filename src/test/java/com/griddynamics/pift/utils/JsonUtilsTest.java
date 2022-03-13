package com.griddynamics.pift.utils;

import com.griddynamics.pift.model.PiftProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void assertJsonTest() {
        TestClass testClass = new TestClass();
        testClass.name = "fieldValue";
        Map<String, Object> params = new HashMap<>();
        params.put("entity", testClass);
        String actualJson = "{\"field\" : \"fieldValue\"}";
        JsonUtils.assertJson("/json/templ.jsont", actualJson, params);
    }

    static class TestClass {
        String name;
    }

    @Test
    void getPropertiesTest() {
        assertDoesNotThrow(JsonUtils::getProperties);
        PiftProperties properties = JsonUtils.getProperties();
        assertNotNull(properties);
    }
}