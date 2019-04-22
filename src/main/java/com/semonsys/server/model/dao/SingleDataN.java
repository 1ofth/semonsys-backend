package com.semonsys.server.model.dao;

import com.semonsys.shared.DataType;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.DefaultValue;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Deprecated
public class SingleDataN implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private Long time;

    @Setter @Getter
    private String dataTypeName;

    @Setter @Getter
    private String groupName;


    @Getter
    private Object value;
    @Getter
    private com.semonsys.shared.DataType type = com.semonsys.shared.DataType.NONE;


    public void setValue(final String value) {
        type = com.semonsys.shared.DataType.STRING;
        this.value = value;
    }

    public void setValue(final Long value) {
        type = com.semonsys.shared.DataType.LONG;
        this.value = value;
    }

    public void setValue(final Double value) {
        type = com.semonsys.shared.DataType.DOUBLE;
        this.value = value;
    }

    public void setValue(final List<SingleDataN> value) {
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

        SingleDataN that = (SingleDataN) o;
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
