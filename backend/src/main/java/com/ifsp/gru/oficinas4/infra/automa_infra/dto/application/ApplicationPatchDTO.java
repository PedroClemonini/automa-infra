package com.ifsp.gru.oficinas4.infra.automa_infra.dto.application;

import jakarta.validation.constraints.Size;

public record ApplicationPatchDTO(
        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String name,

        @Size(max = 5000, message = "A descrição deve ter no máximo 5000 caracteres")
        String description,

        @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
        String status,

        String sshUser,

        String sshPassword
) {
    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasStatus() {
        return status != null && !status.isBlank();
    }

    public boolean hasSshUser() {
        return sshUser != null && !sshUser.isBlank();
    }

    public boolean hasSshPassword() {
        return sshPassword != null && !sshPassword.isBlank();
    }
}