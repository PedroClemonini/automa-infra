package com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetResourceTypeUseCase {

    private final ResourceTypeRepositoryPort repository;

    @Transactional(readOnly = true)
    public ResourceType execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceType não encontrado com ID: " + id));
    }
}