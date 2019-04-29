package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;

public class IdentifierTO {
    @SerializedName(value = "v")
    private String value;

    public IdentifierTO() {}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
