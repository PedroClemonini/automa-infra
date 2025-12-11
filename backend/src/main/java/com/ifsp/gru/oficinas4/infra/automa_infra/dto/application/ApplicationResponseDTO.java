package com.ifsp.gru.oficinas4.infra.automa_infra.dto.application;

import java.time.LocalDateTime;

public record ApplicationResponseDTO(
        Long id,
        Long userId,
        String userName,
        String name,
        String description,
        String status,
        String sshUser,
        String ipAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastDeployedAt
) {}