package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ApplicationResourceJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ResourceJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationResourceRepository extends JpaRepository<ApplicationResourceJpaEntity, Long> {

    boolean existsByApplicationIdAndResourcesId(Long applicationId, Long resourceId);

    @Query("SELECT ar.resources FROM ApplicationResourceJpaEntity ar WHERE ar.application.id = :applicationId")
    List<ResourceJpaEntity> findResourcesByApplicationId(@Param("applicationId") Long applicationId);

    // Na interface ApplicationResourceRepository (JPA)
    Page<ApplicationResourceJpaEntity> findByApplicationId(Long applicationId, Pageable pageable);

    List<ApplicationResourceJpaEntity> findByApplicationId(Long applicationId);
}