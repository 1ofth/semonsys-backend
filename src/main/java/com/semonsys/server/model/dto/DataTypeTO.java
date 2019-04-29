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

    public Boolean getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Boolean monitoring) {
        this.monitoring = monitoring;
    }

    public static DataTypeTO convert(DataType object){
        DataTypeTO dataTypeTO = new DataTypeTO();

        dataTypeTO.setDescription(object.getDescription());
        dataTypeTO.setName(object.getName());
        dataTypeTO.setMonitoring(object.getMonitoring());

        return dataTypeTO;
    }
}
