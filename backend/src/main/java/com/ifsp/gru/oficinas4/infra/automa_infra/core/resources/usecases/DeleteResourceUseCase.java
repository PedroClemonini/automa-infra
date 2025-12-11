package com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteResourceUseCase {

    private final ResourceRepositoryPort resourceRepository;

    @Transactional
    public void execute(Long id) {
        if (!resourceRepository.existsById(id)) {
            // Pode retornar false ou lançar exceção, dependendo da sua preferência
            throw new ResourceNotFoundException("Recurso não encontrado para exclusão: " + id);
        }
        resourceRepository.deleteById(id);
    }
}