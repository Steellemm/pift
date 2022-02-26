package com.griddynamics.pift.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.model.PiftProperties;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    public static String getJsonAsString(String fileName) {
        try(JsonParser jsonParser = JSON_MAPPER.createParser(getJsonInputStream(fileName))) {
            return jsonParser.readValueAsTree().toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static InputStream getJsonFile(String filePath){
        return getJsonInputStream(filePath);
    }

    public static String objectToJson(Object actualObject) {
        try {
            return JSON_MAPPER.writeValueAsString(actualObject);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static InputStream getJsonInputStream(String path){
        return JsonUtils.class.getResourceAsStream(path);
    }

    public static PiftProperties getProperties() {
        try(JsonParser jsonParser = YAML_MAPPER.createParser(getYaml())) {
            return jsonParser.readValueAs(PiftProperties.class);
        } catch (IOException e) {
            throw new IllegalStateException("Exception during file reading", e);
        }
    }

    private static InputStream getYaml(){
        return JsonUtils.class.getResourceAsStream("/pift.yaml");
    }

}
