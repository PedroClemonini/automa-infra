package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ApplicationJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {
    private final UserMapper userMapper;

    public ApplicationJpaEntity toJpaEntity(Application domain) {
        if (domain == null) return null;
        ApplicationJpaEntity entity = new ApplicationJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setSshUser(domain.getSshUser());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setDescription(domain.getDescription());
        entity.setCreatedBy(userMapper.toJpaEntity(domain.getCreatedBy()));
        entity.setIpAddress(domain.getIpAddress());
        entity.setSshPassword(domain.getSshPassword());
        entity.setLastDeployedAt(domain.getLastDeployedAt());
        entity.setStatus(domain.getStatus());

        return entity;
    }

    public Application toDomain(ApplicationJpaEntity entity) {
        if (entity == null) return null;
        Application domain = new Application();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setStatus(entity.getStatus());
        domain.setSshUser(entity.getSshUser());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setDescription(entity.getDescription());
        domain.setCreatedBy(userMapper.toDomain(entity.getCreatedBy()));
        domain.setIpAddress(entity.getIpAddress());
        domain.setSshPassword(entity.getSshPassword());
        domain.setLastDeployedAt(entity.getLastDeployedAt());
        domain.setStatus(entity.getStatus());
        return domain;
    }
}
