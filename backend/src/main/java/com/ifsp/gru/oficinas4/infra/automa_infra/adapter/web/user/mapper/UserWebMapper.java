package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.mapper;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserLoginRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserRegisterRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {

    // --- Conversão de Requisições para Domínio ---

    public User toDomain(UserRegisterRequest request) {
        if (request == null) return null;

        User domain = new User();
        domain.setName(request.getName());
        domain.setEmail(request.getEmail());
        domain.setPassword(request.getPassword()); // Senha em texto puro, será criptografada no Use Case
        domain.setRole(request.getRole());

        return domain;
    }

    // Não é necessário um mapper para UserLoginRequest, pois o AuthenticateUseCase
    // recebe email e senha como Strings (já é o que o DTO fornece).

    // --- Conversão de Domínio para Resposta ---

    public UserResponse toResponse(User domain) {
        if (domain == null) return null;

        UserResponse response = new UserResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setEmail(domain.getEmail());
        response.setRole(domain.getRole());

        // **NUNCA INCLUIR SENHA AQUI**

        return response;
    }
}