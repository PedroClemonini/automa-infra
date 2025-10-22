package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para ser serializado e enviado como corpo da requisição HTTP POST para a API do Proxmox.
 */
public class CloneRequestDto {

    // O Proxmox espera o parâmetro 'newid'
    @JsonProperty("newid")
    private final int newid;

    // O Proxmox espera o parâmetro 'name'
    @JsonProperty("name")
    private String name;

    // O Proxmox espera o parâmetro 'full' (1 para full clone, 0 para linked clone)
    @JsonProperty("full")
    private Integer full;

    // O WebClient do Spring o converterá para JSON: {"newid": 201, "name": "vm-nome", "full": 1}

    public CloneRequestDto(int newid, String name, boolean isFullClone) {
        this.newid = newid;
        this.name = name;
        this.full = isFullClone ? 1 : 0;
    }

    // --- Getters (Essenciais para a serialização) ---

    public int getNewid() {
        return newid;
    }

    public String getName() {
        return name;
    }

    public Integer getFull() {
        return full;
    }

    // **NOTA:** Sem Setters, pois os valores são definidos no construtor.
}