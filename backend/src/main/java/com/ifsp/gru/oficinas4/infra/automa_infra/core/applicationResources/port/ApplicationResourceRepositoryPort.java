package com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port;


import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApplicationResourceRepositoryPort {
    ApplicationResource save(ApplicationResource applicationResource);
    Optional<ApplicationResource> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByApplicationIdAndResourceId(Long applicationId, Long resourceId);
    List<List<String>> findCodeSnippetsByApplicationId(Long Id);
    Page<ApplicationResource> findAll(Pageable pageable);
    Page<ApplicationResource> findByApplicationId(Long applicationId, Pageable pageable);
    long count();
}