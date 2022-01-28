package com.griddynamics.pift.model;

import lombok.Data;

import java.util.Map;

@Data
public class PiftProperties {
    private Map<String, Table> tables;
}
