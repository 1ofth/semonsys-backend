package com.semonsys.server.model.dao;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "data")
public class SingleData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_id_seq")
    @SequenceGenerator(name = "data_id_seq", sequenceName = "data_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private Server server = null;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_type_id")
    private DataType dataType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_group_id")
    private DataGroup dataGroup;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "param_id")
    private Param param;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comp_id")
    private CompositeData compositeData;

    private Long time;

    public SingleData() {}
}
