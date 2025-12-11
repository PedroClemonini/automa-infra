package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserResponse; // Assumindo que você terá um UserResponse
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String sshUser;
    private String ipAddress;

    // Relacionamento
    private UserResponse createdBy; // Retorna o User simplificado

    // Datas de Auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastDeployedAt;

    // NOTA: SSH Password NUNCA deve ser retornado no Response
}