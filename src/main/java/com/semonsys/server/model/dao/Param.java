package com.semonsys.server.model.dao;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Param {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "param_id_seq")
    @SequenceGenerator(name = "param_id_seq", sequenceName = "param_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "int_value")
    private Long longValue;

    @Column(name = "float_value")
    private Double doubleValue;

    @Column(name = "text_value")
    private String stringValue;

    public Param() {}
}
