package com.danamon.persistence.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.YearMonth;
import java.util.Date;

@Entity
@Table(name = "transaction_header")
@Data
public class TransactionHeader extends BaseWithoutId{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "danamon_transaction_header_sequence")
    @SequenceGenerator(name = "danamon_transaction_header_sequence", sequenceName = "danamon_transaction_header_sequence",
            allocationSize = 1,initialValue=1)
    private Integer id;

    @Column
    private Date periode;

    @ManyToOne
    @JoinColumn(name = "customer_information_id")
    private CustomerInformation customerInformation;
}
