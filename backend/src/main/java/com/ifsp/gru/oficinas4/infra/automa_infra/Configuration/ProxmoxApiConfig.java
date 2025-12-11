package com.ifsp.gru.oficinas4.infra.automa_infra.Configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Mapeia propriedades que começam com 'proxmox.api' no application.properties/yaml
@Component
@ConfigurationProperties(prefix = "proxmox.api")
@Getter
@Setter
public class ProxmoxApiConfig {

    // Configurações do Proxmox API
    private String endpoint;
    private String apiToken;
    private boolean insecure = true; // Valor default, mas pode ser sobrescrito

    // Configurações SSH (usadas no bloco provider "proxmox")
    private String sshUsername;
    private String sshPassword;

    // Configurações do nó (Node)
    private String nodeName;
    private String nodeAddress;
}