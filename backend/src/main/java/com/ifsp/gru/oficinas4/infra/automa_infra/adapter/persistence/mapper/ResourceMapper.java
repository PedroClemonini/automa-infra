package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ResourceJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Lombok: Injeta o ResourceTypeMapper automaticamente
public class ResourceMapper {

    private final ResourceTypeMapper resourceTypeMapper;

    // Converte Domínio -> JPA (Para salvar no banco)
    public ResourceJpaEntity toJpaEntity(Resource domain) {
        if (domain == null) {
            return null;
        }

        ResourceJpaEntity entity = new ResourceJpaEntity();

        // Campos simples
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setVersion(domain.getVersion());
        entity.setCodeSnippet(domain.getCodeSnippet()); // A conversão List<String> é tratada na Entity (via Converter)
        entity.setActive(domain.getActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Relacionamento com ResourceType
        if (domain.getResourceType() != null) {
            entity.setResourceType(resourceTypeMapper.toJpaEntity(domain.getResourceType()));
        }

        return entity;
    }

    // Converte JPA -> Domínio (Para usar nas Regras de Negócio)
    public Resource toDomain(ResourceJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Resource domain = new Resource();

        // Campos simples
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setVersion(entity.getVersion());
        domain.setCodeSnippet(entity.getCodeSnippet());
        domain.setActive(entity.getActive());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        // Relacionamento com ResourceType
        if (entity.getResourceType() != null) {
            domain.setResourceType(resourceTypeMapper.toDomain(entity.getResourceType()));
        }

        return domain;
    }
}