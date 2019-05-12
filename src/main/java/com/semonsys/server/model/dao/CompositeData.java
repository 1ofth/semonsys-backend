package com.semonsys.server.model.dao;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "composite_data")
public class CompositeData {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compositeData")
    private List<SingleData> data = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "composite_data_id_seq")
    @SequenceGenerator(name = "composite_data_id_seq", sequenceName = "composite_data_id_seq", allocationSize = 1)
    private Long id;
    private String identifier;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private Server server;
}
