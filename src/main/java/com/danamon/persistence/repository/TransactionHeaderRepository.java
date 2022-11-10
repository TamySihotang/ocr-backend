package com.danamon.persistence.repository;

import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.persistence.domain.TransactionHeader;;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TransactionHeaderRepository extends BaseWithoutIdRepository<TransactionHeader> {
    Optional<TransactionHeader> findByPeriodeAndCustomerInformation(Date periode, CustomerInformation customerInformation);
}
