package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para receber a requisição JSON do cliente no Controller.
 */
public class ReceivedVmInfoDto {

    // Nó Proxmox onde a clonagem deve ocorrer
    @JsonProperty("node_name")
    private String nodeName;

    // ID da VM de origem (Template)
    @JsonProperty("source_vmid")
    private int sourceVmid;

    // Novo ID desejado para a VM clonada
    @JsonProperty("new_vmid")
    private int newVmid;

    // Novo nome desejado para a VM clonada
    @JsonProperty("vm_name")
    private String vmName;

    // Outros campos úteis (Ex: RAM, Cores)

    // Construtor padrão
    public ReceivedVmInfoDto() {}

    // Construtor completo (opcional)
    public ReceivedVmInfoDto(String nodeName, int sourceVmid, int newVmid, String vmName) {
        this.nodeName = nodeName;
        this.sourceVmid = sourceVmid;
        this.newVmid = newVmid;
        this.vmName = vmName;
    }

    // --- Getters (Essenciais para a aplicação) ---

    public String getNodeName() {
        return nodeName;
    }

    public int getSourceVmid() {
        return sourceVmid;
    }

    public int getNewVmid() {
        return newVmid;
    }

    public String getVmName() {
        return vmName;
    }

    // Setters (Essenciais para o Jackson/Spring mapear o JSON)
    // ... (omiti Setters para brevidade, mas devem existir ou usar Records no Java 16+)
}