package com.ifsp.gru.oficinas4.infra.automa_infra.dto;


/**
 * DTO para padronizar a resposta enviada de volta ao cliente.
 */
public class ResponseDto {

    private final String message;
    private final String proxmoxTaskId; // O UPID retornado pelo Proxmox

    public ResponseDto(String message, String proxmoxTaskId) {
        this.message = message;
        this.proxmoxTaskId = proxmoxTaskId;
    }

    // --- Getters ---

    public String getMessage() {
        return message;
    }

    public String getProxmoxTaskId() {
        return proxmoxTaskId;
    }
}