package com.griddynamics.uspanov.test.Entities;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "location")
    private String location;

    @OneToOne(mappedBy = "department")
    private Entity entity;
}
