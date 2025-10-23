package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Schema(description = "VM a ser criada")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id gerado automaticamente pela banco de dados para identificar a VM")
    private Long appId;

    @Schema(description = "Nome da VM escolhido pelo usuario")
    @NotBlank(message = "O nome da aplicação deve ser preenchido")
    private String appName;

    @Schema(description = "Nome do usuario")
    @NotBlank(message = "O nome do usuario deve ser preenchido")
    private String username;

    @Schema(description = "Senha do usuario")
    @NotBlank(message = "O senha do usuario deve ser preenchida")
    private String password;

    @Schema(description = "Ip da VM gerado junto com a maquina virtual")
    private String ip;

    @jakarta.validation.constraints.NotNull(message = "Selecione 1 aplicação")
    @ManyToOne
    @JoinColumn(name = "app_service_id")
    @Schema(description = "Aplicacao escolhida pelo usuario para ser instalada junto a VM")
    private ServiceApplication appService;

    @Schema(description ="Banco de dados instalado na VM")
    @OneToOne
    @JoinColumn(name = "app_database")
    private DatabaseApplication appDatabase;

    public Application(){};

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public DatabaseApplication getAppDatabase() {
        return appDatabase;
    }

    public void setAppDatabase(DatabaseApplication appDatabase) {
        this.appDatabase = appDatabase;
    }

    public ServiceApplication getAppService() {
        return appService;
    }

    public void setAppService(ServiceApplication appService) {
        this.appService = appService;
    }
}
