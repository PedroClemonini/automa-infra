package com.ifsp.gru.oficinas4.infra.automa_infra.service;


import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourcePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    // ================= READ =================

    public Page<ResourceResponseDTO> findAll(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    public Optional<ResourceResponseDTO> findById(Long id) {
        return resourceRepository.findById(id)
                .map(this::toResponseDTO);
    }


    public Page<ResourceResponseDTO> searchByName(String name, Pageable pageable) {
        return resourceRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponseDTO);
    }

    public Page<ResourceResponseDTO> findByResourceTypeId(Long resourceTypeId, Pageable pageable) {
        return resourceRepository.findByResourceTypeId(resourceTypeId, pageable)
                .map(this::toResponseDTO);
    }

    public Page<ResourceResponseDTO> findByActive(Boolean active, Pageable pageable) {
        return resourceRepository.findByActive(active, pageable)
                .map(this::toResponseDTO);
    }

    // ================= CREATE =================

    @Transactional
    public ResourceResponseDTO create(ResourceRequestDTO dto) {
        // Verifica se o ResourceType existe
        ResourceType resourceType = resourceTypeRepository.findById(dto.resourceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de recurso não encontrado com ID: " + dto.resourceTypeId()
                ));


        Resource resource = new Resource();
        resource.setResourceType(resourceType);
        resource.setName(dto.name());
        resource.setDescription(dto.description());
        resource.setVersion(dto.version());
        resource.setCodeSnippet(dto.codeSnippet());
        resource.setActive(dto.active() != null ? dto.active() : true);
        resource.setCreatedAt(LocalDateTime.now());

        Resource saved = resourceRepository.save(resource);
        return toResponseDTO(saved);
    }

    // ================= UPDATE (PATCH) =================

    @Transactional
    public Optional<ResourceResponseDTO> update(Long id, ResourcePatchDTO dto) {
        return resourceRepository.findById(id).map(resource -> {

            // Atualiza resourceType se fornecido
            if (dto.resourceTypeId() != null) {
                ResourceType resourceType = resourceTypeRepository.findById(dto.resourceTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Tipo de recurso não encontrado com ID: " + dto.resourceTypeId()
                        ));
                resource.setResourceType(resourceType);
            }

            // Atualiza nome se fornecido
            if (dto.name() != null && !dto.name().isBlank()) {
                resource.setName(dto.name());
            }

            // Atualiza descrição se fornecida
            if (dto.description() != null && !dto.description().isBlank()) {
                resource.setDescription(dto.description());
            }

            // Atualiza versão se fornecida
            if (dto.version() != null && !dto.version().isBlank()) {
                resource.setVersion(dto.version());
            }

            // Atualiza codeSnippet se fornecido
            if (dto.codeSnippet() != null && !dto.codeSnippet().isBlank()) {
                resource.setCodeSnippet(dto.codeSnippet());
            }

            // Atualiza active se fornecido
            if (dto.active() != null) {
                resource.setActive(dto.active());
            }

            resource.setUpdatedAt(LocalDateTime.now());

            Resource updated = resourceRepository.save(resource);
            return toResponseDTO(updated);
        });
    }

    // ================= TOGGLE ACTIVE =================

    @Transactional
    public Optional<ResourceResponseDTO> toggleActive(Long id) {
        return resourceRepository.findById(id).map(resource -> {
            resource.setActive(!resource.getActive());
            resource.setUpdatedAt(LocalDateTime.now());
            Resource updated = resourceRepository.save(resource);
            return toResponseDTO(updated);
        });
    }

    // ================= DELETE =================

    @Transactional
    public boolean delete(Long id) {
        if (!resourceRepository.existsById(id)) {
            return false;
        }
        resourceRepository.deleteById(id);
        return true;
    }

    // ================= UTILITY =================

    public boolean exists(Long id) {
        return resourceRepository.existsById(id);
    }



    public long count() {
        return resourceRepository.count();
    }

    public long countByActive(Boolean active) {
        return resourceRepository.countByActive(active);
    }

    // ================= MAPPER =================

    private ResourceResponseDTO toResponseDTO(Resource resource) {
        return new ResourceResponseDTO(
                resource.getId(),
                resource.getResourceType().getId(),
                resource.getResourceType().getName(),
                resource.getName(),
                resource.getDescription(),
                resource.getVersion(),
                resource.getCodeSnippet(),
                resource.getActive(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }
}