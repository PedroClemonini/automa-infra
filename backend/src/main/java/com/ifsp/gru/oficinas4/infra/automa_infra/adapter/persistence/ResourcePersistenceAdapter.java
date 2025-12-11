package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ResourceJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper.ResourceMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository.ResourceRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.port.ResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResourcePersistenceAdapter implements ResourceRepositoryPort {

    private final ResourceRepository repository;
    private final ResourceMapper mapper;

    @Override
    public Resource save(Resource resource) {
        ResourceJpaEntity entity = mapper.toJpaEntity(resource);
        ResourceJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Page<Resource> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Resource> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Resource> findByResourceTypeId(Long id, Pageable pageable) {
        return repository.findByResourceTypeId(id, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Resource> findByActive(Boolean active, Pageable pageable) {
        return repository.findByActive(active, pageable).map(mapper::toDomain);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long countByActive(Boolean active) {
        return repository.countByActive(active);
    }
}