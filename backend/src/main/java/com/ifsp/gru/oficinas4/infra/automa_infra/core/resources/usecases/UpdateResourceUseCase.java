package com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.port.ResourceTypeRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UpdateResourceUseCase {

    private final ResourceRepositoryPort resourceRepository;
    private final ResourceTypeRepositoryPort resourceTypeRepository;

    @Transactional
    public Resource execute(Long id, Resource incomingData) {

        Resource existingResource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado com ID: " + id));


        if (incomingData.getResourceType() != null && incomingData.getResourceType().getId() != null) {
            Long newTypeId = incomingData.getResourceType().getId();

            ResourceType newResourceType = resourceTypeRepository.findById(newTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de recurso não encontrado com ID: " + newTypeId));

            existingResource.setResourceType(newResourceType);
        }


        if (hasText(incomingData.getName())) {
            existingResource.setName(incomingData.getName());
        }

        if (hasText(incomingData.getDescription())) {
            existingResource.setDescription(incomingData.getDescription());
        }

        if (hasText(incomingData.getVersion())) {
            existingResource.setVersion(incomingData.getVersion());
        }

        if (incomingData.getCodeSnippet() != null) {
            existingResource.setCodeSnippet(incomingData.getCodeSnippet());
        }

        if (incomingData.getActive() != null) {
            existingResource.setActive(incomingData.getActive());
        }

        existingResource.setUpdatedAt(LocalDateTime.now());

        return resourceRepository.save(existingResource);
    }

    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }
}