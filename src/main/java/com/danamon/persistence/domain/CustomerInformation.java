package com.danamon.persistence.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "customer_information")
@Data
public class CustomerInformation extends BaseWithoutId{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "danamon_customer_information_sequence")
    @SequenceGenerator(name = "danamon_customer_information_sequence", sequenceName = "danamon_customer_information_sequence",
            allocationSize = 1,initialValue=1)
    private Integer id;

    @Column
    private String nama;

    @Column
    private String alamat;

    @Column
    private String bank;

    @Column(name = "nomor_rekening")
    private String nomorRekening;
}
