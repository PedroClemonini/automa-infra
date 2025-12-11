package com.ifsp.gru.oficinas4.infra.automa_infra.core.application.usecase;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ListApplicationsUseCase {

    private final ApplicationRepositoryPort applicationRepository;


    @Transactional(readOnly = true)
    public Page<Application> execute(String name, String status, Long userId, Pageable pageable) {


        if (name != null && status != null) {
            return applicationRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        }

        if (userId != null && status != null) {
            return applicationRepository.findByCreatedByIdAndStatus(userId, status, pageable);
        }

        // 2. Filtros de campo único
        if (name != null) {
            return applicationRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        if (status != null) {
            return applicationRepository.findByStatus(status, pageable);
        }

        if (userId != null) {
            return applicationRepository.findByCreatedById(userId, pageable);
        }


        return applicationRepository.findAll(pageable);
    }
}