package com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource;

import jakarta.validation.constraints.Size;

public record ApplicationResourcePatchDTO(
        Long applicationId,
        Long resourceId
) {}