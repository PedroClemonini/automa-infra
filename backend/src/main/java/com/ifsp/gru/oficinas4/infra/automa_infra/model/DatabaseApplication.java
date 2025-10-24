package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Schema(description = "Banco de dados que sera instalado na VM")
public class DatabaseApplication {
    @Schema(description = "Id gerado automaticamente para o banco de dados")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do banco de dados")
    private String name;
    @Schema(description = "Tipo do banco de dados")
    private String type;
    @Schema(description = "Porta na qual o banco de dados esta localizado")
    private int port;


    public DatabaseApplication(){};
}
