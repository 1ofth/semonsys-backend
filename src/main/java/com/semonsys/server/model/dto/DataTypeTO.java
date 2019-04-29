package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;

public class DataTypeTO {
    @SerializedName(value = "n")
    private String name = "";
    @SerializedName(value = "d")
    private String description = "";

    public DataTypeTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
