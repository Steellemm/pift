package com.griddynamics.pift.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ck_entity")
@Getter
@Setter
public class CKEntity {

    @EmbeddedId
    private CompositeKey id;
    private String info;

}
