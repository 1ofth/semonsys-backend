package com.semonsys.server.model.dao;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "data")
@SecondaryTable(name = "param")
public class SingleData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_id_seq")
    @SequenceGenerator(name = "data_id_seq", sequenceName = "data_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private Server server = null;

    private Long time;


    @Column(table = "param", name = "int_value")
    private Long longValue;

    @Column(table = "param", name = "float_value")
    private Double doubleValue;

    @Column(table = "param", name = "text_value")
    private String stringValue;

    public SingleData() {}
}
