package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.dto;



import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationResourceRequest {

    @NotNull(message = "O ID da Aplicação é obrigatório")
    private Long applicationId;

    @NotNull(message = "O ID do Recurso é obrigatório")
    private Long resourceId;
}