package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;

public class DataGroupTO {
    @SerializedName(value = "n")
    private String name = "";
    @SerializedName(value = "d")
    private String description = "";

    public DataGroupTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
