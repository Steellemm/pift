package com.griddynamics.pift.model;

import lombok.Data;

import java.util.Map;

@Data
public class PiftProperties {
    private Template template;
    private Map<String, Table> tables;
}
