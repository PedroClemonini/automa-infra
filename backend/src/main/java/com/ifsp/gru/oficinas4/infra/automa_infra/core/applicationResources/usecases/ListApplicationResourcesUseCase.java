package com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ListApplicationResourcesUseCase {

    private final ApplicationResourceRepositoryPort repository;

    /**
     * Lista os vínculos de recursos de uma aplicação específica ou todos os vínculos, se o ID for nulo.
     *
     * @param applicationId O ID da Application para filtrar os vínculos (opcional).
     * @param pageable Configuração de paginação.
     * @return Uma Page contendo os objetos ApplicationResource.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationResource> execute(Long applicationId, Pageable pageable) {

        if (applicationId != null) {
            // Caso de uso principal: Listar recursos de uma aplicação específica
            // NOTA: Assumindo que o repositório tem o método findByApplicationId.
            return repository.findByApplicationId(applicationId, pageable);
        }

        // Caso de uso secundário: Listar todos os vínculos do sistema (se for permitido)
        return repository.findAll(pageable);
    }
}