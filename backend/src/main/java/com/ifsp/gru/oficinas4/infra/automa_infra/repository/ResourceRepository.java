package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // Buscar por nome (com paginação)
    Page<Resource> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Buscar por tipo de recurso
    Page<Resource> findByResourceTypeId(Long resourceTypeId, Pageable pageable);

    // Buscar por status ativo
    Page<Resource> findByActive(Boolean active, Pageable pageable);

    // Contar por status ativo
    long countByActive(Boolean active);
}