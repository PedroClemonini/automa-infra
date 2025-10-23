package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appId;

    @NotBlank(message = "O nome da aplicação deve ser preenchido")
    private String appName;

    @NotBlank(message = "O nome do usuario deve ser preenchido")
    private String username;

    @NotBlank(message = "O senha do usuario deve ser preenchida")
    private String password;

    private String ip;

    @Getter
    @ManyToOne
    @JoinColumn(name = "app_service_id")
    @NotBlank(message = "Selecione 1 aplicação")
    private ServiceApplication appService;

    @Getter
    @ManyToOne
    @JoinColumn(name = "database_application_id")
    private DatabaseApplication databaseApplication;

    public void setDatabaseApplication(DatabaseApplication databaseApplication) {
        this.databaseApplication = databaseApplication;
    }

    public void setAppService(ServiceApplication appService) {
        this.appService = appService;
    }

    public Application(){};

}
