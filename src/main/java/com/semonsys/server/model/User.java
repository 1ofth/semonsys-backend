package com.semonsys.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private String email;

    @ElementCollection
    @CollectionTable(name = "tokens",
        joinColumns = @JoinColumn(name = "user_login"))
    private List<String> refreshTokens;

    private Boolean verified;
    private String verificationToken;

    public User(final String login, final String password) {
        this.login = login;
        this.password = password;
    }
}
