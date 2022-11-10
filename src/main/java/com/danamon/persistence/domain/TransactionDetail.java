package com.danamon.persistence.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction_detail")
@Data
public class TransactionDetail extends BaseWithoutId{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "danamon_transaction_detail_sequence")
    @SequenceGenerator(name = "danamon_transaction_detail_sequence", sequenceName = "danamon_transaction_detail_sequence",
            allocationSize = 1,initialValue=1)
    private Integer id;

    @Column(name = "tanggal_transaksi")
    private Date tanggalTransaksi;

    @Column
    private String keterangan;

    @Column
    private String cbg;

    @Column
    private Double debet;

    @Column
    private Double kredit;

    @Column
    private Double saldo;

    @ManyToOne
    @JoinColumn(name = "transaction_header_id")
    private TransactionHeader transactionHeader;
}
