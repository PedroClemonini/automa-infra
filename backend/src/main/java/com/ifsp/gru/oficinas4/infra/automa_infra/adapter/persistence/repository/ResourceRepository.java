package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ResourceJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceJpaEntity, Long> {

    // Busca pelo ID da relação ManyToOne (resourceType.id)
    Page<ResourceJpaEntity> findByResourceTypeId(Long resourceTypeId, Pageable pageable);

    Page<ResourceJpaEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<ResourceJpaEntity> findByActive(Boolean active, Pageable pageable);

    long countByActive(Boolean active);
}