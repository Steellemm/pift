package com.griddynamics.pift.model;

import lombok.Data;

@Data
public class Column {
    private FieldType type;
    private String format;
    private Condition condition;
}
