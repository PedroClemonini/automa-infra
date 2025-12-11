package com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateResourceUseCase {

    private final ResourceRepositoryPort resourceRepository;
    private final ResourceTypeRepositoryPort resourceTypeRepository;

    @Transactional
    public Resource execute(Resource dto) {
        ResourceType resourceType = resourceTypeRepository.findById(dto.getResourceType().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de recurso não encontrado com ID: " + dto.getResourceType().getId()
                ));

        // 2. Monta Entidade de Domínio
        Resource resource = new Resource();
        resource.setResourceType(resourceType);
        resource.setName(dto.getName());
        resource.setDescription(dto.getDescription());
        resource.setVersion(dto.getVersion());
        resource.setCodeSnippet(dto.getCodeSnippet());

        // Regra de negócio: Default true se nulo
        resource.setActive(dto.getActive() != null ? dto.getActive() : true);

        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now()); // Inicializa updated com created

        // 3. Persiste
        return resourceRepository.save(resource);
    }
}