package com.semonsys.server.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SingleDataTO {
    private long time = -1;
    private String value = "";

    public SingleDataTO() {}
}