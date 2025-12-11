package com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ListResourcesUseCase {

    private final ResourceRepositoryPort resourceRepository;

    @Transactional(readOnly = true)
    public Page<Resource> execute(String name, Long typeId, Boolean active, Pageable pageable) {

        if (name != null && !name.isBlank()) {
            return resourceRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        if (typeId != null) {
            return resourceRepository.findByResourceTypeId(typeId, pageable);
        }

        if (active != null) {
            return resourceRepository.findByActive(active, pageable);
        }

        return resourceRepository.findAll(pageable);
    }
}