package com.griddynamics.pift;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;

class PiftPropertiesManagerTest {

    PiftPropertiesManager piftPropertiesManager = new PiftPropertiesManager();

    @ParameterizedTest
    @CsvSource({
            "templ,json/templ.jsont",
            "templ.json,json/templ.json"
    })
    void getPathToTemplate(String fileName, String expectedPath) {
        Path pathToTemplate = piftPropertiesManager.getPathToTemplate(fileName);
        Assertions.assertEquals(expectedPath, pathToTemplate.toString());
    }

}