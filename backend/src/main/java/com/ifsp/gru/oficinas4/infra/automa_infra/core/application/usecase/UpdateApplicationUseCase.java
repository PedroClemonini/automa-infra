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
public class UpdateApplicationUseCase {

    private final ApplicationRepositoryPort applicationRepository;

    @Transactional
    public Application execute(Long id, Application incomingData) {

        Application existingApp = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada: " + id));

        if (hasText(incomingData.getName())) {
            existingApp.setName(incomingData.getName());
        }

        if (incomingData.getDescription() != null) {
            existingApp.setDescription(incomingData.getDescription());
        }

        if (hasText(incomingData.getStatus())) {
            existingApp.setStatus(incomingData.getStatus());
        }

        if (hasText(incomingData.getSshUser())) {
            existingApp.setSshUser(incomingData.getSshUser());
        }

        if (hasText(incomingData.getSshPassword())) {
            existingApp.setSshPassword(incomingData.getSshPassword());
        }

        // 3. Atualiza Timestamp
        existingApp.setUpdatedAt(LocalDateTime.now());

        // 4. Salva (Update)
        return applicationRepository.save(existingApp);
    }

    // Método auxiliar
    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }
}