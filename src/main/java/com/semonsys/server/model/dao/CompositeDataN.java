package com.semonsys.server.model.dao;

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
public class CompositeDataN implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SingleDataN> data;
    private String name;
}
