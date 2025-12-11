package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.dto.ResourceTypeResponse; // Reutilizando se possível
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceResponse {
    private Long id;
    private String name;
    private String description;
    private String version;
    private List<String> codeSnippet;
    private Boolean active;

    // Devolvemos os dados do Tipo para o front-end não precisar fazer outra requisição
    private ResourceTypeResponse resourceType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}