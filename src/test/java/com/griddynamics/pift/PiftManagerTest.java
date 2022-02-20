package com.griddynamics.pift;

import com.griddynamics.pift.PiftManager;
import com.griddynamics.pift.model.PiftProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PiftManagerTest {

    @Test
    void getPropertiesTest() {
        assertDoesNotThrow(PiftManager::getProperties);
        PiftProperties properties = PiftManager.getProperties();
        assertNotNull(properties);
    }

}