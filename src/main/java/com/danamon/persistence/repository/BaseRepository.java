package com.danamon.persistence.repository;

import com.danamon.persistence.domain.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.Optional;

/**
 * Created By Dewi 10/08/21
 */

@NoRepositoryBean
public interface BaseRepository<T extends Base> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {
    Optional<T> findBySecureId(String secureId);
}