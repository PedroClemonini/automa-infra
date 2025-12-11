package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.mapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.dto.ResourceTypeRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.dto.ResourceTypeResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import org.springframework.stereotype.Component;

@Component
public class ResourceTypeWebMapper {

    // Request (DTO) -> Domínio
    public ResourceType toDomain(ResourceTypeRequest request) {
        ResourceType domain = new ResourceType();
        domain.setName(request.getName());
        domain.setDescription(request.getDescription());
        return domain;
    }

    // Domínio -> Response (DTO)
    public ResourceTypeResponse toResponse(ResourceType domain) {
        if (domain == null) return null;

        ResourceTypeResponse response = new ResourceTypeResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setDescription(domain.getDescription());
        return response;
    }
}