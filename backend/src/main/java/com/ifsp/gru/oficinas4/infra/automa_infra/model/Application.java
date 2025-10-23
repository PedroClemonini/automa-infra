package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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

    @NotBlank(message = "Selecione 1 aplicação")
    private ServiceApplication appService;

    @OneToOne
    @JoinColumn(name = "app_database_id")
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
