package com.griddynamics.pift.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class SuperEntity {

    @Column
    private String address;

}
