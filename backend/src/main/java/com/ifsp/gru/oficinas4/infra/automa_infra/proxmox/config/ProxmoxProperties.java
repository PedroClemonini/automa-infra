package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(value = "proxmox.api")
public class ProxmoxProperties {
    private String host;
    private String user;
    private String realm;
    private String token;
    private String tokenName;
}
