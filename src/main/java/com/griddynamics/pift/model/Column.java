package com.griddynamics.pift.model;

import lombok.Data;

@Data
public class Column {
    private FieldType type;
    private Condition condition;
}
