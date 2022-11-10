package com.danamon.persistence.repository;

import com.danamon.persistence.domain.BaseWithoutId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.Optional;

/**
 * Created By Dewi 10/08/21
 */

@NoRepositoryBean
public interface BaseWithoutIdRepository<T extends BaseWithoutId> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {
    Optional<T> findBySecureId(String secureId);
}