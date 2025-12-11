package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ApplicationJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper.ApplicationMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository.ApplicationRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicationPersistenceAdapter implements ApplicationRepositoryPort {

    private final ApplicationRepository repository;
    private final ApplicationMapper mapper;

    // --- CRUD BÁSICO ---

    @Override
    public Application save(Application application) {
        ApplicationJpaEntity entity = mapper.toJpaEntity(application);
        ApplicationJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Application> findById(Long id) {
        // Usa o .map() do Optional para converter a Entidade JPA em Domínio se existir
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // --- VERIFICAÇÃO DE UNICIDADE ---

    @Override
    public boolean existsByNameAndCreatedById(String name, Long userId) {
        return repository.existsByNameAndCreatedById(name, userId);
    }

    // --- LISTAGEM GERAL E FILTRADA (PAGINADA) ---

    @Override
    public Page<Application> findAll(Pageable pageable) {
        // Retorna Page<Entity> do JPA e mapeia para Page<Domain>
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Application> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Application> findByStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Application> findByCreatedById(Long userId, Pageable pageable) {
        return repository.findByCreatedById(userId, pageable).map(mapper::toDomain);
    }

    // --- BUSCAS COMBINADAS ---

    @Override
    public Page<Application> findByCreatedByIdAndStatus(Long userId, String status, Pageable pageable) {
        return repository.findByCreatedByIdAndStatus(userId, status, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Application> findByNameContainingIgnoreCaseAndStatus(String name, String status, Pageable pageable) {
        return repository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable).map(mapper::toDomain);
    }

    // --- BUSCAS POR DATA ---

    @Override
    public Page<Application> findByCreatedAtAfter(LocalDateTime date, Pageable pageable) {
        return repository.findByCreatedAtAfter(date, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Application> findByLastDeployedAtAfter(LocalDateTime date, Pageable pageable) {
        return repository.findByLastDeployedAtAfter(date, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Application> findByLastDeployedAtIsNull(Pageable pageable) {
        return repository.findByLastDeployedAtIsNull(pageable).map(mapper::toDomain);
    }

    // --- LÓGICA COMPLEXA / AVANÇADA ---

    @Override
    public Page<Application> findApplicationsNeedingAttention(LocalDateTime createdBefore, Pageable pageable) {
        // Exemplo: Aplicações criadas antes da data e nunca deployadas
        // Assumindo que o Repositório JPA tem o método:
        // return repository.findByCreatedAtBeforeAndLastDeployedAtIsNull(createdBefore, pageable).map(mapper::toDomain);

        // Retorna vazio se a lógica não estiver implementada no JPA Repository ainda
        return Page.empty(pageable);
    }

    // --- CONTAGEM / MÉTRICAS ---

    @Override
    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }

    @Override
    public long countByCreatedById(Long userId) {
        return repository.countByCreatedById(userId);
    }

    @Override
    public long countByCreatedByIdAndStatus(Long userId, String status) {
        return repository.countByCreatedByIdAndStatus(userId, status);
    }
}