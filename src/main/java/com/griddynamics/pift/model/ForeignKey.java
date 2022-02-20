package com.griddynamics.pift.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ForeignKey {

    @JsonProperty("table")
    private String tableName;

    @JsonProperty("column")
    private String columnName;
}
