package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServiceApplicationDTO(
        @NotBlank
        String name,
        @NotBlank
        String server,
        @NotNull
        int port
) {
}
