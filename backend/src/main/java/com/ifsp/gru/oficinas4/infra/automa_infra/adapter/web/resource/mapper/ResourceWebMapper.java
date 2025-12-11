package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.mapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto.ResourceRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto.ResourceResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.mapper.ResourceTypeWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceWebMapper {

    private final ResourceTypeWebMapper resourceTypeWebMapper;

    public Resource toDomain(ResourceRequest request) {
        if (request == null) return null;

        Resource domain = new Resource();
        domain.setName(request.getName());
        domain.setDescription(request.getDescription());
        domain.setVersion(request.getVersion());
        domain.setCodeSnippet(request.getCodeSnippet());

        if (request.getActive() != null) {
            domain.setActive(request.getActive());
        }

        // Cria um objeto ResourceType apenas com o ID para passar ao UseCase
        if (request.getResourceTypeId() != null) {
            ResourceType type = new ResourceType();
            type.setId(request.getResourceTypeId());
            domain.setResourceType(type);
        }

        return domain;
    }

    public ResourceResponse toResponse(Resource domain) {
        if (domain == null) return null;

        ResourceResponse response = new ResourceResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setDescription(domain.getDescription());
        response.setVersion(domain.getVersion());
        response.setCodeSnippet(domain.getCodeSnippet());
        response.setActive(domain.getActive());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());

        // Usa o mapper do ResourceType para converter o objeto aninhado
        if (domain.getResourceType() != null) {
            response.setResourceType(resourceTypeWebMapper.toResponse(domain.getResourceType()));
        }

        return response;
    }
}
