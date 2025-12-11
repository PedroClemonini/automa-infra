package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ApplicationResourceJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper.ApplicationResourceMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository.ApplicationResourceRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationResourcePersistenceAdapter implements ApplicationResourceRepositoryPort {

    private final ApplicationResourceRepository repository;
    private final ApplicationResourceMapper mapper;

    @Override
    public ApplicationResource save(ApplicationResource applicationResource) {
        ApplicationResourceJpaEntity entity = mapper.toJpaEntity(applicationResource);
        ApplicationResourceJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ApplicationResource> findById(Long id) {
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
    public boolean existsByApplicationIdAndResourceId(Long applicationId, Long resourceId) {
        // Assumindo que o nome do método no JPA Repository é existsByApplicationIdAndResourcesId
        return repository.existsByApplicationIdAndResourcesId(applicationId, resourceId);
    }

    @Override
    public List<List<String>> findCodeSnippetsByApplicationId(Long applicationId) {

        List<ApplicationResourceJpaEntity> entities = repository.findByApplicationId(applicationId);

        // 2. Converte para domínio, extrai o Resource e depois os Snippets
        return entities.stream()
                .map(mapper::toDomain)              // ApplicationResource (Domain)
                .map(ApplicationResource::getResources) // Resource (Domain)
                .filter(Objects::nonNull)           // Evita NullPointerException se não houver resource
                .map(Resource::getCodeSnippet)     // List<String>
                .collect(Collectors.toList());      // List<List<String>>
    }

    @Override
    public Page<ApplicationResource> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Page<ApplicationResource> findByApplicationId(Long applicationId, Pageable pageable) {

        // 1. Chama o método do JPA Repository com o Pageable
        return repository.findByApplicationId(applicationId, pageable)

                // 2. Converte a Page de Entidades para a Page de Domínio
                .map(mapper::toDomain);
    }
}