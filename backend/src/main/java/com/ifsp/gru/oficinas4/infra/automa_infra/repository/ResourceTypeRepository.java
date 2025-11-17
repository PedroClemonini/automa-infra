package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, Long> {

    // Busca por nome contendo, ignorando maiúsculas/minúsculas
    Page<ResourceType> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Verifica duplicação
    boolean existsByNameIgnoreCase(String name);


    Optional<ResourceType> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

}
