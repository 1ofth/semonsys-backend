package com.semonsys.server.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "composite_data")
public class CompositeData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "composite_data_id_seq")
    @SequenceGenerator(name = "composite_data_id_seq", sequenceName = "composite_data_id_seq", allocationSize = 1)
    private Long id;

    private String identifier;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private Server server;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compositeData")
    List<SingleData> data = new ArrayList<>();
}
