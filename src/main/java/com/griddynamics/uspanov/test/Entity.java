package com.griddynamics.uspanov.test;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;


@Setter
@Getter
@javax.persistence.Entity
public class Entity {
    @Column
    private String name;
    @Column
    private Long number;
    @Column
    private Integer age;
    @Column
    private BigDecimal count;

}

