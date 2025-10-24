package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain;
import lombok.Data;

@Data
public class ProxmoxClone {

    private int newid;
    private String name;
    private String target;
    private String storage;
    private Boolean full;
    private String description;
}