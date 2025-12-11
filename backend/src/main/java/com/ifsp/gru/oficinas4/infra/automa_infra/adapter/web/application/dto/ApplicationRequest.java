package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationRequest {

    // Dados obrigatórios na criação
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "O ID do usuário criador é obrigatório")
    private Long createdById; // ID do User, será resolvido no Use Case

    // Dados de Conexão (podem ser nulos ou preenchidos)
    private String sshUser;
    private String sshPassword;
    private String ipAddress;

    // Status (pode ser definido ou default 'DRAFT' no Use Case)
    private String status;
}