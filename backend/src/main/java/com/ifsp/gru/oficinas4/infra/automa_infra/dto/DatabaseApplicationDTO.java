package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DatabaseApplicationDTO(
        @NotBlank
         String name,
        @NotBlank
         String type,
         @NotNull
         int port
) {
}
