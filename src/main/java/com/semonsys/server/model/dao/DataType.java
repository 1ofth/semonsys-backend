package com.semonsys.server.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "data_type")
@AllArgsConstructor
@NoArgsConstructor
public class DataType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_type_id_seq")
    @SequenceGenerator(name = "data_type_id_seq", sequenceName = "data_type_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(unique = true)
    private String name;

    private String description;
    private Boolean monitoring;

    public DataType(final long id, final String userLogin, final String name, final String description) {
        this.id = id;
        this.userLogin = userLogin;
        this.name = name;
        this.description = description;
    }
}
