package com.griddynamics.pift;


import com.griddynamics.pift.utils.JsonUtils;
import com.griddynamics.pift.utils.TemplateUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

public class AssertionHelper {

    private final EntityMap entityMap;
    private final PiftPropertiesManager piftPropertiesManager;

    public AssertionHelper(EntityMap entityMap, PiftPropertiesManager piftPropertiesManager) {
        this.entityMap = entityMap;
        this.piftPropertiesManager = piftPropertiesManager;
    }

    public void assertJsonEquals(String fileName, Object json) {
        assertJsonEquals(fileName, JsonUtils.objectToJson(json));
    }

    public void assertJsonEquals(String fileName, String json) {
        try {
            JSONAssert.assertEquals(TemplateUtils.getJsonAsString(
                    JsonUtils.getJsonInputStream(piftPropertiesManager.getPathToTemplate(fileName)),
                    entityMap.getEntityMap()), json, false);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }
}
