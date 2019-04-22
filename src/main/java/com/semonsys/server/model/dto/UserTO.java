package com.semonsys.server.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserTO {
    private String login = "";
    private String email = "";
    private boolean verified = false;

    public UserTO() {}
}
