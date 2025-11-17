package com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource;

import java.time.LocalDateTime;

public record ResourceResponseDTO(
        Long id,
        Long resourceTypeId,
        String resourceTypeName,
        String name,
        String description,
        String version,
        String codeSnippet,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}