package com.ifsp.gru.oficinas4.infra.automa_infra.dto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRequestDTO(
        @NotNull(message = "O ID do usuário é obrigatório")
        Long userId,

        @NotBlank(message = "O nome da aplicação é obrigatório")
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String name,

        @Size(max = 5000, message = "A descrição deve ter no máximo 5000 caracteres")
        String description,

        @NotBlank(message = "O status é obrigatório")
        @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
        String status,

        @NotBlank(message = "O usuário SSH é obrigatório")
        String sshUser,

        @NotBlank(message = "A senha SSH é obrigatória")
        String sshPassword
) {}