package com.danamon.persistence.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "company")
@Data
public class Company extends BaseWithoutId{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "danamon_company_sequence")
    @SequenceGenerator(name = "danamon_company_sequence", sequenceName = "danamon_company_sequence",
            allocationSize = 1,initialValue=1)
    private Integer id;

    @Column
    private String perusahaan;

    @Column
    private String komoditi;
}
