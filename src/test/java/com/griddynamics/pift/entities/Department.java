package com.griddynamics.pift.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@javax.persistence.Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "location")
    private String location;

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", location='" + location + '\'' +
                '}';
    }
}
