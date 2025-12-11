package com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// ResourceTypeRepositoryPort.java
public interface ResourceTypeRepositoryPort {
    ResourceType save(ResourceType resourceType);
    Optional<ResourceType> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNameIgnoreCase(String name);

    Page<ResourceType> findAll(Pageable pageable);
    Page<ResourceType> findByNameContainingIgnoreCase(String name, Pageable pageable);
}