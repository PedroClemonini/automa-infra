package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ResourceRequest {

    @NotNull(message = "O ID do tipo de recurso é obrigatório")
    private Long resourceTypeId;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255)
    private String name;

    private String description;

    @Size(max = 50)
    private String version;

    private List<String> codeSnippet;

    private Boolean active; // Opcional, default geralmente é true no domínio
}