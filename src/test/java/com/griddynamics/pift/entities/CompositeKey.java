package com.griddynamics.pift.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class CompositeKey {
    private String name;
    private Long departmentId;
}
