package com.danamon.persistence.repository;

import com.danamon.persistence.domain.Company;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends BaseWithoutIdRepository<Company> {
    List<Company> findByPerusahaanLikeIgnoreCase(String name);
}
