package com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.dto.ResourceTypeRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;

import com.ifsp.gru.oficinas4.infra.automa_infra.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateResourceTypeUseCase {

    private final ResourceTypeRepositoryPort repository;

    @Transactional
    public ResourceType execute(ResourceType incomingRequest) {
        if (repository.existsByNameIgnoreCase(incomingRequest.getName())) {
            throw new DuplicateResourceException("Já existe um ResourceType com nome: " + incomingRequest.getName());
        }

        ResourceType rt = new ResourceType();
        rt.setName(incomingRequest.getName());
        rt.setDescription(incomingRequest.getDescription());

        return repository.save(rt);
    }
}