package com.griddynamics.pift.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter
@javax.persistence.Entity
@Table(name = "entity")
public class Entity extends SuperEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "number")
    private Long number;

    private String name;

    @Column
    private Integer age;

    @Column
    @Transient
    private Date date;

    @Column
    private Timestamp timestamp;

    @Column
    private LocalDate localDate;

    @Column
    private LocalDateTime localDateTime;

    @Column
    private BigDecimal count;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "dept_id", referencedColumnName = "id")
    private Long department;

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", date=" + date +
                ", timestamp=" + timestamp +
                ", localDate=" + localDate +
                ", localDateTime=" + localDateTime +
                ", count=" + count +
                ", department=" + department +
                '}';
    }
}

