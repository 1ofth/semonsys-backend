package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;
import com.semonsys.server.model.dao.SingleData;

import java.util.Objects;

public class SingleDataTO {
    @SerializedName(value = "p")
    private ParamTO param = null;
    @SerializedName(value = "t")
    private String type = "";
    @SerializedName(value = "m")
    private boolean monitoring = true;

    public SingleDataTO() {}

    public String getType(){
        return type;
    }

    public void setType(final String type){
        this.type = type;
    }

    public ParamTO getParam(){
        return param;
    }

    public void setParam(final ParamTO param){
        this.param = param;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(final boolean monitoring) {
        this.monitoring = monitoring;
    }

    public static SingleDataTO convert(SingleData data){
        SingleDataTO singleDataTO = new SingleDataTO();

        singleDataTO.setType(data.getDataType().getName());
        singleDataTO.setMonitoring(data.getDataType().getMonitoring());

        ParamTO param = ParamTO.convert(data.getParam(), data.getTime());

        singleDataTO.setParam(param);

        return singleDataTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleDataTO that = (SingleDataTO) o;
        return monitoring == that.monitoring &&
            param.equals(that.param) &&
            type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(param, type, monitoring);
    }
}