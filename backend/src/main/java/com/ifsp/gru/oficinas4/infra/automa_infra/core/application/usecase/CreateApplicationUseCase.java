package com.ifsp.gru.oficinas4.infra.automa_infra.core.application.usecase;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.user.port.UserRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateApplicationUseCase {

    private final ApplicationRepositoryPort applicationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort; // Mantemos a injeção para buscar o User no Core


    @Transactional
    public Application execute(Application application, Long userId) {

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));


        application.setCreatedBy(user);

        LocalDateTime now = LocalDateTime.now();
        application.setCreatedAt(now);
        application.setUpdatedAt(now);


        if (application.getStatus() == null || application.getStatus().isBlank()) {
            application.setStatus("DRAFT"); // Exemplo
        }

        // 4. Persistência via Porta
        return applicationRepositoryPort.save(application);
    }
}