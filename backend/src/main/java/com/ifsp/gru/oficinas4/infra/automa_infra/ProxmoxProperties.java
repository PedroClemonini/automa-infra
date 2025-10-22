package com.ifsp.gru.oficinas4.infra.automa_infra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Mapeia as configurações do Proxmox VE API do arquivo application.properties.
 * O prefixo 'proxmox.api' é usado para carregar as propriedades.
 */
@Component
@ConfigurationProperties(prefix = "proxmox.api")
public class ProxmoxProperties {

    private String host;
    private String user;
    private String realm; // Ex: 'pam' ou 'pve'
    private String tokenId;
    private String secret;
    private int port = 8006; // Porta padrão do Proxmox API

    // Getters e Setters (Necessários para @ConfigurationProperties)
    // O Spring usará os Setters para injetar os valores das propriedades.

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Constrói o cabeçalho completo de Autorização do API Token Proxmox.
     * Formato: PVEAPIToken=USER@REALM!TOKENID=SECRET
     * @return String formatada para o cabeçalho Authorization.
     */
    public String getApiTokenHeader() {
        return String.format("PVEAPIToken=%s@%s!%s=%s", user, realm, tokenId, secret);
    }
}