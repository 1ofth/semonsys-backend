package com.semonsys.server.model.dao;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "server")
@Data
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_id_seq")
    @SequenceGenerator(name = "server_id_seq", sequenceName = "server_id_seq", allocationSize = 1)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_login")
    private User user;


    private String name = "";
    private String description = "";
    private String ip = "";
    private Integer port = 10_000;


    private Boolean activated = false;
    @Column(name = "act_data")
    private String activationData = "";

    public Server() {}
}
