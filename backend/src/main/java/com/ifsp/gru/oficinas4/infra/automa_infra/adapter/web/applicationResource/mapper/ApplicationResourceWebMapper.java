package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.mapper;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.mapper.ApplicationWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.dto.ApplicationResourceRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.dto.ApplicationResourceResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.mapper.ResourceWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationResourceWebMapper {

    private final ApplicationWebMapper applicationMapper;
    private final ResourceWebMapper resourceMapper;

    // Request (DTO) -> Domínio (Usado para Update/Patch, contém apenas IDs)
    public ApplicationResource toDomain(ApplicationResourceRequest request) {
        if (request == null) return null;

        ApplicationResource domain = new ApplicationResource();

        // No Core (Use Case), ele usará esses IDs para buscar os objetos completos.
        if (request.getApplicationId() != null) {
            Application app = new Application();
            app.setId(request.getApplicationId());
            domain.setApplication(app);
        }

        if (request.getResourceId() != null) {
            Resource resource = new Resource();
            resource.setId(request.getResourceId());
            domain.setResources(resource);
        }

        return domain;
    }

    // Domínio -> Response (DTO)
    public ApplicationResourceResponse toResponse(ApplicationResource domain) {
        if (domain == null) return null;

        ApplicationResourceResponse response = new ApplicationResourceResponse();
        response.setId(domain.getId());
        response.setAddedAt(domain.getAddedAt());

        if (domain.getApplication() != null) {
            response.setApplication(applicationMapper.toResponse(domain.getApplication()));
        }

        if (domain.getResources() != null) {
            response.setResources(resourceMapper.toResponse(domain.getResources()));
        }

        return response;
    }
}