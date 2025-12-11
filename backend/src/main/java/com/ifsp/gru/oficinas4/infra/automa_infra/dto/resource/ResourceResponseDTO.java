package com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource;

import java.time.LocalDateTime;
import java.util.List;

public record ResourceResponseDTO(
        Long id,
        Long resourceTypeId,
        String resourceTypeName,
        String name,
        String description,
        String version,
        List<String> codeSnippet,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}