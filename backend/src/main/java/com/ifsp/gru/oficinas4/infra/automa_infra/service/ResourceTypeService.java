package com.ifsp.gru.oficinas4.infra.automa_infra.service;


import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.DuplicateResourceException;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ResourceTypeService {

    @Autowired
    private ResourceTypeRepository repository;

    // ------------------- MAPEAMENTO -------------------

    private ResourceTypeResponseDTO toResponseDTO(ResourceType rt) {
        return new ResourceTypeResponseDTO(
                rt.getId(),
                rt.getName(),
                rt.getDescription()
        );
    }

    private ResourceType toEntity(ResourceTypeRequestDTO dto) {
        ResourceType rt = new ResourceType();
        rt.setName(dto.name());
        rt.setDescription(dto.description());
        return rt;
    }

    // ------------------- LISTAR -------------------

    @Transactional(readOnly = true)
    public Page<ResourceTypeResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ResourceTypeResponseDTO> searchByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponseDTO);
    }

    // ------------------- BUSCAR POR ID -------------------

    @Transactional(readOnly = true)
    public Optional<ResourceTypeResponseDTO> findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ResourceTypeResponseDTO findByIdOrThrow(Long id) {
        ResourceType rt = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceType não encontrado com ID: " + id));
        return toResponseDTO(rt);
    }

    // ------------------- CRIAR -------------------

    @Transactional
    public ResourceTypeResponseDTO create(ResourceTypeRequestDTO dto) {

        if (repository.existsByNameIgnoreCase(dto.name())) {
            throw new DuplicateResourceException("Já existe um ResourceType com nome: " + dto.name());
        }

        ResourceType rt = toEntity(dto);

        ResourceType saved = repository.save(rt);

        return toResponseDTO(saved);
    }


    @Transactional
    public Optional<ResourceTypeResponseDTO> update(Long id, ResourceTypePatchDTO dto) {
        Optional<ResourceType> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        ResourceType existing = optional.get();

        // ✅ Atualiza NOME apenas se não for null e não estiver vazio
        if (dto.name() != null && !dto.name().isBlank()) {
            // Verifica duplicação apenas se o nome está sendo alterado
            if (!existing.getName().equalsIgnoreCase(dto.name())
                    && repository.existsByNameIgnoreCase(dto.name())) {
                throw new DuplicateResourceException(
                        "Nome já cadastrado: " + dto.name()
                );
            }
            existing.setName(dto.name());
        }

        // ✅ Atualiza DESCRIÇÃO apenas se não for null e não estiver vazio
        if (dto.description() != null && !dto.description().isBlank()) {
            existing.setDescription(dto.description());
        }

        ResourceType updated = repository.save(existing);

        return Optional.of(toResponseDTO(updated));
    }

    @Transactional
    public ResourceTypeResponseDTO updateOrThrow(Long id, ResourceTypePatchDTO dto) {
        return update(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceType não encontrado com ID: " + id));
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

    @Transactional
    public void deleteOrThrow(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ResourceType não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    // ------------------- EXISTS -------------------

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean nameExists(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}
