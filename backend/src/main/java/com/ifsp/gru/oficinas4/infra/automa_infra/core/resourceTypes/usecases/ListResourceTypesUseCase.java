package com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ListResourceTypesUseCase {

    private final ResourceTypeRepositoryPort repository;

    @Transactional(readOnly = true)
    public Page<ResourceType> execute(String name, Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name, pageable);
        }

        return repository.findAll(pageable);
    }
}