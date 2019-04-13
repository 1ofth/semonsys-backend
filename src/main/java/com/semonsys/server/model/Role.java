package com.semonsys.server.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "role")
public class Role {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    @Id
    @Column(length = 10)
    private String role;
}
