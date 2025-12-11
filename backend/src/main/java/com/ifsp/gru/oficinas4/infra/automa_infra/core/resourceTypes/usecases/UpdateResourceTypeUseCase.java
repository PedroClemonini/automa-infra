package com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;

import com.ifsp.gru.oficinas4.infra.automa_infra.exception.DuplicateResourceException;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateResourceTypeUseCase {

    private final ResourceTypeRepositoryPort repository;

    @Transactional
    public ResourceType execute(Long id, ResourceType incomingRequest) {
        ResourceType existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceType não encontrado com ID: " + id));

        // Atualiza Nome (com validação de duplicidade)
        if (incomingRequest.getName() != null && !incomingRequest.getName().isBlank()) {
            // Se o nome mudou E o novo nome já existe -> Erro
            if (!existing.getName().equalsIgnoreCase(incomingRequest.getName())
                    && repository.existsByNameIgnoreCase(incomingRequest.getName())) {
                throw new DuplicateResourceException("Nome já cadastrado: " + incomingRequest.getName());
            }
            existing.setName(incomingRequest.getName());
        }

        // Atualiza Descrição
        if (incomingRequest.getDescription() != null && !incomingRequest.getDescription().isBlank()) {
            existing.setDescription(incomingRequest.getDescription());
        }

        return repository.save(existing);
    }
}