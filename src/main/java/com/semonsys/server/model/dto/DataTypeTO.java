package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;
import com.semonsys.server.model.dao.DataType;

public class DataTypeTO {
    @SerializedName(value = "n")
    private String name = "";
    @SerializedName(value = "d")
    private String description = "";
    @SerializedName(value = "m")
    private Boolean monitoring = false;

    public DataTypeTO() {
    }

    public static DataTypeTO convert(final DataType object) {
        DataTypeTO dataTypeTO = new DataTypeTO();

        dataTypeTO.setDescription(object.getDescription());
        dataTypeTO.setName(object.getName());
        dataTypeTO.setMonitoring(object.getMonitoring());

        return dataTypeTO;
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

    public Boolean getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(final Boolean monitoring) {
        this.monitoring = monitoring;
    }
}
