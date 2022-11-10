package com.danamon.persistence.repository;

import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TransactionDetailRepository extends BaseWithoutIdRepository<TransactionDetail> {
    @Modifying
    @Transactional
    @Query(value = "delete from TransactionDetail d where d.transactionHeader = :transactionHeader ")
    void deleteByTransactionHeader(TransactionHeader transactionHeader);

    List<TransactionDetail> findByTransactionHeaderAndKeteranganIn(TransactionHeader transactionHeader, List<String> keterangan);
    List<TransactionDetail> findByTransactionHeader(TransactionHeader transactionHeader);

    List<TransactionDetail> findByTransactionHeaderIn(List<TransactionHeader> transactionHeaderList);
}
