package com.ifsp.gru.oficinas4.infra.automa_infra.model;

/**
 * Classe de domínio que representa uma Máquina Virtual (VM) gerenciada pela aplicação.
 * Se houver persistência em banco de dados, esta classe se tornaria uma @Entity.
 */
public class VirtualMachine {

    private final String name;
    private final int vmid;
    private final String node;
    private String status; // Ex: 'running', 'stopped', 'cloning'

    /**
     * Construtor para criar um objeto VirtualMachine.
     * @param name O nome da VM.
     * @param vmid O ID único da VM no Proxmox.
     * @param node O nó Proxmox onde a VM está hospedada.
     */
    public VirtualMachine(String name, int vmid, String node) {
        if (vmid <= 0) {
            throw new IllegalArgumentException("VMID deve ser positivo.");
        }
        this.name = name;
        this.vmid = vmid;
        this.node = node;
        this.status = "unknown"; // Status inicial
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public int getVmid() {
        return vmid;
    }

    public String getNode() {
        return node;
    }

    public String getStatus() {
        return status;
    }

    // --- Setter ---
    // Permite atualizar o status, possivelmente após consultar a API do Proxmox.
    public void setStatus(String status) {
        this.status = status;
    }

    // --- Método de Exemplo ---
    public String getFullPath() {
        return String.format("nodes/%s/qemu/%d", node, vmid);
    }

    @Override
    public String toString() {
        return String.format("VM [ID: %d, Nome: %s, Nó: %s, Status: %s]", vmid, name, node, status);
    }
}