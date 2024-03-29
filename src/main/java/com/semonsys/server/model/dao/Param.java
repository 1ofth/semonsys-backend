package com.semonsys.server.model.dao;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "param")
public class Param {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "param_id_seq")
    @SequenceGenerator(name = "param_id_seq", sequenceName = "param_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "int_value")
    private Long longValue = null;

    @Column(name = "float_value")
    private Double doubleValue = null;

    @Column(name = "text_value")
    private String stringValue = null;

    public Param() {
    }
}
