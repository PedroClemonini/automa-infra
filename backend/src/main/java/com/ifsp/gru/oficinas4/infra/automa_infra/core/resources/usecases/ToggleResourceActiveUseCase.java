package com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.usecases;




import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ToggleResourceActiveUseCase {

    private final ResourceRepositoryPort resourceRepository;

    @Transactional
    public Resource execute(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado: " + id));

        // Inverte o status
        resource.setActive(!Boolean.TRUE.equals(resource.getActive()));
        resource.setUpdatedAt(LocalDateTime.now());

        return resourceRepository.save(resource);
    }
}