package com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.usecases;


import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteResourceTypeUseCase {

    private final ResourceTypeRepositoryPort repository;

    @Transactional
    public void execute(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ResourceType não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}