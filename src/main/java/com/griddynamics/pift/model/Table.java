package com.griddynamics.pift.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Table {

    private Map<String, Column> columns;

    @JsonProperty("foreign-keys")
    private Map<String, String> foreignKeys;

}
