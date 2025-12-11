package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.mapper;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto.ApplicationRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto.ApplicationResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.mapper.UserWebMapper; // Assumindo este mapper
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationWebMapper {

    private final UserWebMapper userWebMapper;

    // Request (DTO) -> Domínio
    public Application toDomain(ApplicationRequest request) {
        if (request == null) return null;

        Application domain = new Application();
        domain.setName(request.getName());
        domain.setDescription(request.getDescription());
        domain.setStatus(request.getStatus());
        domain.setSshUser(request.getSshUser());
        domain.setSshPassword(request.getSshPassword());
        domain.setIpAddress(request.getIpAddress());

        // NOTA: O createdBy (User completo) NÃO é definido aqui, apenas o ID está no DTO.
        // O Use Case (CreateApplicationUseCase) é que fará a busca do User pelo ID.

        return domain;
    }

    // Domínio -> Response (DTO)
    public ApplicationResponse toResponse(Application domain) {
        if (domain == null) return null;

        ApplicationResponse response = new ApplicationResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setDescription(domain.getDescription());
        response.setStatus(domain.getStatus());
        response.setSshUser(domain.getSshUser());
        response.setIpAddress(domain.getIpAddress());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());
        response.setLastDeployedAt(domain.getLastDeployedAt());

        if (domain.getCreatedBy() != null) {
            response.setCreatedBy(userWebMapper.toResponse(domain.getCreatedBy()));
        }

        return response;
    }
}