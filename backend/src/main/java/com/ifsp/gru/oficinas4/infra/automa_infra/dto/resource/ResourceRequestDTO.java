package com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ResourceRequestDTO(
        @NotNull(message = "ID do tipo de recurso é obrigatório")
        Long resourceTypeId,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String name,

        @Size(max = 5000, message = "Descrição deve ter no máximo 5000 caracteres")
        String description,

        @Size(max = 50, message = "Versão deve ter no máximo 50 caracteres")
        String version,

        @Size(max = 10000, message = "Code snippet deve ter no máximo 10000 caracteres")
        List<String> codeSnippet,

        Boolean active // Se null, será tratado como true no service
) {}