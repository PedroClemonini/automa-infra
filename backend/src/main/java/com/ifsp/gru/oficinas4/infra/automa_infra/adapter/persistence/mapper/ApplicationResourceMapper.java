package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ApplicationResourceJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationResourceMapper {

    // Injeção dos Mappers das entidades relacionadas para converter a árvore completa
    private final ApplicationMapper applicationMapper;
    private final ResourceMapper resourceMapper;

    // Converte Domínio -> JPA (Para salvar no banco)
    public ApplicationResourceJpaEntity toJpaEntity(ApplicationResource domain) {
        if (domain == null) {
            return null;
        }

        ApplicationResourceJpaEntity entity = new ApplicationResourceJpaEntity();

        entity.setId(domain.getId());
        entity.setAddedAt(domain.getAddedAt());

        // Mapeamento da Aplicação
        if (domain.getApplication() != null) {
            entity.setApplication(applicationMapper.toJpaEntity(domain.getApplication()));
        }

        // Mapeamento do Recurso
        if (domain.getResources() != null) {
            entity.setResources(resourceMapper.toJpaEntity(domain.getResources()));
        }

        return entity;
    }

    // Converte JPA -> Domínio (Para usar nas Regras de Negócio)
    public ApplicationResource toDomain(ApplicationResourceJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        ApplicationResource domain = new ApplicationResource();

        domain.setId(entity.getId());
        domain.setAddedAt(entity.getAddedAt());

        // Mapeamento da Aplicação
        if (entity.getApplication() != null) {
            domain.setApplication(applicationMapper.toDomain(entity.getApplication()));
        }

        // Mapeamento do Recurso
        if (entity.getResources() != null) {
            domain.setResources(resourceMapper.toDomain(entity.getResources()));
        }

        return domain;
    }
}