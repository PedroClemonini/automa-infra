package com.ifsp.gru.oficinas4.infra.automa_infra;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@OpenAPIDefinition(info = @Info(title = "Provisioner API", version = "1.0", description = "Documentação da API de Infraestrutura"))
@SpringBootApplication
public class AutomaInfraApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutomaInfraApplication.class, args);
    }
}