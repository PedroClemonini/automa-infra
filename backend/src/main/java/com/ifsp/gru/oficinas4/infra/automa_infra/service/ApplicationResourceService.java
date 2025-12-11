package com.ifsp.gru.oficinas4.infra.automa_infra.service;


import com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource.ApplicationResourcePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource.ApplicationResourceRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource.ApplicationResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationResourceRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ApplicationResourceService {

    @Autowired
    private ApplicationResourceRepository repository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    // ------------------- MAPEAMENTO -------------------

    private ApplicationResourceResponseDTO toResponseDTO(ApplicationResource ar) {
        return new ApplicationResourceResponseDTO(
                ar.getId(),
                ar.getApplication().getId(),
                ar.getResources().getId(),
                ar.getAddedAt()
        );
    }

    private ApplicationResource toEntity(ApplicationResourceRequestDTO dto) {
        Application application = applicationRepository.findById(dto.applicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Aplicação não encontrada com ID: " + dto.applicationId()));

        Resource resource = resourceRepository.findById(dto.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado com ID: " + dto.resourceId()));

        return ApplicationResource.builder()
                .application(application)
                .resources(resource)
                .addedAt(LocalDateTime.now())
                .build();
    }

    // ------------------- LISTAR -------------------

    @Transactional(readOnly = true)
    public Page<ApplicationResourceResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponseDTO);
    }


    // ------------------- BUSCAR POR ID -------------------

    @Transactional(readOnly = true)
    public Optional<ApplicationResourceResponseDTO> findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ApplicationResourceResponseDTO findByIdOrThrow(Long id) {
        ApplicationResource ar = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo não encontrado com ID: " + id));
        return toResponseDTO(ar);
    }

    // ------------------- CRIAR -------------------

    @Transactional
    public ApplicationResourceResponseDTO create(ApplicationResourceRequestDTO dto) {

        // Impede duplicação, já que existe uniqueConstraint(application_id, resource_id)
        boolean exists = repository.existsByApplicationIdAndResourcesId(dto.applicationId(), dto.resourceId());
        if (exists) {
            throw new IllegalStateException("Este recurso já está vinculado a esta aplicação.");
        }

        ApplicationResource entity = toEntity(dto);
        ApplicationResource saved = repository.save(entity);

        return toResponseDTO(saved);
    }

    // ------------------- ATUALIZAR (PATCH) -------------------

    @Transactional
    public Optional<ApplicationResourceResponseDTO> update(Long id, ApplicationResourcePatchDTO dto) {
        Optional<ApplicationResource> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        ApplicationResource existing = optional.get();

        // Atualizar application
        if (dto.applicationId() != null) {
            Application application = applicationRepository.findById(dto.applicationId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Aplicação não encontrada com ID: " + dto.applicationId()));
            existing.setApplication(application);
        }

        // Atualizar resource
        if (dto.resourceId() != null) {
            Resource resource = resourceRepository.findById(dto.resourceId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Recurso não encontrado com ID: " + dto.resourceId()));
            existing.setResources(resource);
        }

        ApplicationResource updated = repository.save(existing);

        return Optional.of(toResponseDTO(updated));
    }

    // ------------------- DELETE -------------------

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }


    // ------------------- EXISTS -------------------

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return repository.existsById(id);
    }


    // ------------------- CONTAGENS -------------------

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

}
