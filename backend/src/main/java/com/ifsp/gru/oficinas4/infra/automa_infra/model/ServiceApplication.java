package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Schema(description = "Representa um software (serviço) que pode ser instalado em uma VM")
public class ServiceApplication {

    @Schema(description = "Id gerado automaticamente para a aplicacao a ser instalada na VM")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome da aplicacao a ser instalada na VM")
    private String name;

    @Schema(description = "Software do servidor de aplicação que executa o serviço")
    private String server;

    @Schema(description = "Porta na qual esta localizada a aplicacao a ser instalada na VM")
    private int port;

    public ServiceApplication(){}
}

