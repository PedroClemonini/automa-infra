package com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteApplicationResourceUseCase {

    private final ApplicationResourceRepositoryPort repository;

    @Transactional
    public void execute(Long id) {
        if (!repository.existsById(id)) {
            // Optei por lançar exceção para ser mais explícito, mas pode retornar boolean se preferir
            throw new ResourceNotFoundException("Vínculo não encontrado para exclusão: " + id);
        }
        repository.deleteById(id);
    }
}