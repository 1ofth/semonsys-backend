package com.semonsys.server.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "data_group")
@AllArgsConstructor
@NoArgsConstructor
public class DataGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_group_id_seq")
    @SequenceGenerator(name = "data_group_id_seq", sequenceName = "data_group_id_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String description;
}
