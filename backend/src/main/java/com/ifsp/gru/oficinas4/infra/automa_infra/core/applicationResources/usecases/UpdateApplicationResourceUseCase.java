package com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.BusinessException;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateApplicationResourceUseCase {

    private final ApplicationResourceRepositoryPort repository;
    private final ApplicationRepositoryPort applicationRepository;
    private final ResourceRepositoryPort resourceRepository;

    /**
     * Atualiza parcialmente (Patch) um vínculo ApplicationResource existente.
     * @param id O ID do vínculo a ser atualizado.
     * @param incomingData Objeto de domínio ApplicationResource com os novos dados (apenas IDs devem ser preenchidos).
     * @return O vínculo ApplicationResource atualizado.
     */
    @Transactional
    public ApplicationResource execute(Long id, ApplicationResource incomingData) {

        ApplicationResource existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo não encontrado com ID: " + id));


        Long newApplicationId = existing.getApplication().getId();
        Long newResourceId = existing.getResources().getId();


        if (incomingData.getApplication() != null && incomingData.getApplication().getId() != null) {

            Application application = applicationRepository.findById(newApplicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Aplicação não encontrada com ID: " + newApplicationId));
            existing.setApplication(application);
        }


        if (incomingData.getResources() != null && incomingData.getResources().getId() != null) {

            Resource resource = resourceRepository.findById(newResourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado com ID: " + newResourceId));
            existing.setResources(resource);
        }

        if (!newApplicationId.equals(existing.getApplication().getId()) || !newResourceId.equals(existing.getResources().getId())) {
            if (repository.existsByApplicationIdAndResourceId(newApplicationId, newResourceId)) {
                throw new BusinessException("A combinação Application ID " + newApplicationId + " e Resource ID " + newResourceId + " já existe.");
            }
        }

        return repository.save(existing);
    }
}