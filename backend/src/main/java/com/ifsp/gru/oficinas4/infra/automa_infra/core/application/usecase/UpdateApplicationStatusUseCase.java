package com.ifsp.gru.oficinas4.infra.automa_infra.core.application.usecase;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UpdateApplicationStatusUseCase {

    private final ApplicationRepositoryPort applicationRepository;

    @Transactional
    public Application execute(Long id, String newStatus) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada: " + id));

        app.setStatus(newStatus);
        app.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(app);
    }
}