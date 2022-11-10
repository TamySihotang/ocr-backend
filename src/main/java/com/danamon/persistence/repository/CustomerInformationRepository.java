package com.danamon.persistence.repository;

import com.danamon.persistence.domain.CustomerInformation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerInformationRepository extends BaseWithoutIdRepository<CustomerInformation> {
    Optional<CustomerInformation> findByNomorRekeningAndBank(String nomorRekening, String bank);
}
