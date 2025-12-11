package com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public record ApplicationResourceResponseDTO(
        Long id,
        Long applicationId,
        Long resourceId,
        LocalDateTime addedAt
) {}