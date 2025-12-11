package com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationResourceRequestDTO(
        Long applicationId,
        Long resourceId
) {}