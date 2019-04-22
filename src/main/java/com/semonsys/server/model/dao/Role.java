package com.semonsys.server.model.dao;

import lombok.Data;

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
    private String role;
}
