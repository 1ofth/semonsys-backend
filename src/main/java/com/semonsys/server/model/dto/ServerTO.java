package com.semonsys.server.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServerTO {
    private String name = "";
    private String description = "";
    private String ip = "";
    private int port = -1;
    private boolean activated = false;

    public ServerTO() {}
}