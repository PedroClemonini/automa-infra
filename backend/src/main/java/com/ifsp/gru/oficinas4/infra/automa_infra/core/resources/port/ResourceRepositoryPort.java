package com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ResourceRepositoryPort {
    Resource save(Resource resource);
    Optional<Resource> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
    // Métodos de busca
    Page<Resource> findAll(Pageable pageable);
    Page<Resource> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Resource> findByResourceTypeId(Long id, Pageable pageable);
    Page<Resource> findByActive(Boolean active, Pageable pageable);
    long count();
    long countByActive(Boolean active);
}