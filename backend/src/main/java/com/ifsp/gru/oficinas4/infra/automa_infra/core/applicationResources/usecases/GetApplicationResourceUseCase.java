package com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.usecases;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetApplicationResourceUseCase {

    private final ApplicationResourceRepositoryPort repository;

    @Transactional(readOnly = true)
    public ApplicationResource execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo não encontrado com ID: " + id));
    }
}