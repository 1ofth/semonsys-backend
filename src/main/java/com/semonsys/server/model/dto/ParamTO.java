package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;
import com.semonsys.server.model.dao.Param;

import java.util.Objects;

public class ParamTO {

    @SerializedName(value = "v")
    private String value = "";
    @SerializedName(value = "t")
    private long time = 0;

    public ParamTO() {}

    public String getValue() {
        return value;
    }

    public void setValue(String v) {
        this.value = v;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        this.time = t;
    }

    public static ParamTO convert(final Param data, final long time){
        ParamTO param = new ParamTO();

        param.setTime(time);

        if(data.getLongValue() != null) {
            param.setValue(data.getLongValue().toString());
        } else if(data.getDoubleValue() != null){
            param.setValue(data.getDoubleValue().toString());
        } else if(data.getStringValue() != null){
            param.setValue(data.getStringValue());
        }

        return param;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamTO param = (ParamTO) o;
        return time == param.time &&
            value.equals(param.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, time);
    }
}
