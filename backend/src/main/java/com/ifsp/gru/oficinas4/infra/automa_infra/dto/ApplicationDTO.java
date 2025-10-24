package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.DatabaseApplication;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ServiceApplication;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

public record ApplicationDTO(
        @NotBlank(message = "O nome da aplicação deve ser preenchido")
         String appName,

        @NotBlank(message = "O nome do usuario deve ser preenchido")
        String username,

        @NotBlank(message = "O senha do usuario deve ser preenchida")
        String password,

        @NotBlank(message = "Selecione 1 aplicação")
        ServiceApplication appService

) {
}
