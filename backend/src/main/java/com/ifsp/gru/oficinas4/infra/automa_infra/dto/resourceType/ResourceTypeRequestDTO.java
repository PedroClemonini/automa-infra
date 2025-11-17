package com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResourceTypeRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        String name,

        // SEM @NotNull ou @NotBlank - permite null
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String description
) {}