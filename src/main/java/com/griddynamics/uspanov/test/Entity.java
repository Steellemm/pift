package com.griddynamics.uspanov.test;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;


@Setter
@Getter
@javax.persistence.Entity
@Table(name = "Entity_test")
public class Entity {

    private String name;
    @Column
    private Long number;
    @Column
    @Transient
    private Integer age;
    @Column
    private BigDecimal count;

}

