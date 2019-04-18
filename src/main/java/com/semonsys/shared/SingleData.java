package com.semonsys.shared;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;


public class SingleData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Timestamp time = new Timestamp(0);

    public Timestamp getTime() {
        return new Timestamp(time.getTime());
    }

    public void setTime(final Timestamp timestamp) {
        this.time = new Timestamp(timestamp.getTime());
    }

    @Setter @Getter
    private String dataTypeName;

    @Setter @Getter
    private String groupName;


    @Getter
    private Object value;
    @Getter
    private DataType type = DataType.NONE;


    public void setValue(final String value) {
        type = DataType.STRING;
        this.value = value;
    }

    public void setValue(final Long value) {
        type = DataType.LONG;
        this.value = value;
    }

    public void setValue(final Double value) {
        type = DataType.DOUBLE;
        this.value = value;
    }

    public void setValue(final List<SingleData> value) {
        type = DataType.LIST;
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SingleData that = (SingleData) o;
        return Objects.equals(time, that.time)
            && Objects.equals(dataTypeName, that.dataTypeName)
            && Objects.equals(groupName, that.groupName)
            && Objects.equals(value, that.value)
            && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, dataTypeName, groupName, value, type);
    }
}
