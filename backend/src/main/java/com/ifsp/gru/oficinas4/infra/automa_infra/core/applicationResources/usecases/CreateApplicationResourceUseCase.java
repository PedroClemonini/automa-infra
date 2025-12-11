package com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.BusinessException; // Uso BusinessException para regras de negócio (duplicidade)
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateApplicationResourceUseCase {

    private final ApplicationResourceRepositoryPort repository;
    private final ApplicationRepositoryPort applicationRepository;
    private final ResourceRepositoryPort resourceRepository;

    /**
     * Cria um novo vínculo entre uma Application e um Resource.
     * * @param applicationId O ID da Application a ser vinculada.
     * @param resourceId O ID do Resource a ser vinculado.
     * @return O objeto ApplicationResource criado.
     * @throws BusinessException se o vínculo já existir.
     * @throws ResourceNotFoundException se a Application ou o Resource não for encontrado.
     */
    @Transactional
    public ApplicationResource execute(Long applicationId, Long resourceId) {

        // 1. Regra de Negócio: Evitar Duplicidade de Vínculo
        boolean exists = repository.existsByApplicationIdAndResourceId(applicationId, resourceId);
        if (exists) {
            // Refatorei para BusinessException, que mapeia para 400 Bad Request
            throw new BusinessException("O Recurso com ID " + resourceId + " já está vinculado à Aplicação com ID " + applicationId + ".");
        }

        // 2. Buscar Entidades Relacionadas (Validação de Existência)
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicação não encontrada com ID: " + applicationId));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado com ID: " + resourceId));

        // 3. Montar Objeto de Domínio
        ApplicationResource newLink = new ApplicationResource();
        newLink.setApplication(application);
        newLink.setResources(resource); // Assumindo 'setResources' como o nome correto baseado no seu Domain
        newLink.setAddedAt(LocalDateTime.now());

        // 4. Persistir
        return repository.save(newLink);
    }
}