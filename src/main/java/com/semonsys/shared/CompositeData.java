package com.semonsys.shared;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CompositeData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SingleData> data;
    private String name;
}
