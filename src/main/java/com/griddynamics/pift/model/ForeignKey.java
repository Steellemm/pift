package com.griddynamics.pift.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ForeignKey {

    @JsonProperty("table-name")
    private String tableName;

    @JsonProperty("column-name")
    private String columnName;
}
