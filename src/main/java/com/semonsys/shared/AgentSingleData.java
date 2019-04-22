package com.semonsys.shared;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class AgentSingleData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Setter
    private Long time;

    @Setter
    private String dataTypeName;

    @Setter
    private String groupName;


    private Object value;
    private DataType type = DataType.NONE;

    @Setter
    private String compositeDataIdentifier;

    public void setValue(String value) {
        type = DataType.STRING;
        this.value = value;
    }

    public void setValue(Long value) {
        type = DataType.LONG;
        this.value = value;
    }

    public void setValue(Double value) {
        type = DataType.DOUBLE;
        this.value = value;
    }

    public void setValue(List<AgentSingleData> value) {
        type = DataType.LIST;
        this.value = value;
    }


}