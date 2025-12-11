package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.mapper;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.UserJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserJpaEntity entity = new UserJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());

        return entity;
    }

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        User domain = new User();


        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        domain.setRole(entity.getRole());

        return domain;
    }
}