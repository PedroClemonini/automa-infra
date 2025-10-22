package com.ifsp.gru.oficinas4.infra.automa_infra.service;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.ReceivedVmInfoDto;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.CloneRequestDto;
import org.springframework.stereotype.Service;

@Service
public class VirtualMachineService {


    private final ProxmoxService proxmoxService;


    public VirtualMachineService(ProxmoxService proxmoxService) {
        this.proxmoxService = proxmoxService;
    }

    /**
     * Lógica principal para clonar e configurar uma VM.
     * @param vmInformation Dados recebidos do cliente.
     * @return O ID da tarefa (UPID) do Proxmox.
     */
    public String cloneAndConfigureVm(ReceivedVmInfoDto vmInformation) {

        // 1. Extrair os dados necessários para o clone
        String node = vmInformation.getNodeName();           // Nó Proxmox de destino/origem
        int sourceVmid = vmInformation.getSourceVmid();     // ID do Template
        int newVmid = vmInformation.getNewVmid();           // Novo ID da VM
        String vmName = vmInformation.getVmName();          // Novo nome

        // 2. Mapear DTO recebido para o DTO de requisição da API
        CloneRequestDto cloneRequest = new CloneRequestDto(newVmid, vmName, true);

        try {
            // 3. Delegar a chamada HTTP real ao ProxmoxService
            String taskId = proxmoxService.cloneVm(node, sourceVmid, cloneRequest);

            // Aqui  você pode adicionar lógica adicional, como:
            // - Esperar o clone terminar (opcional, com polling no Proxmox Task API)
            // - Iniciar a VM clonada (chamando o endpoint /qemu/{vmid}/status/start)

            System.out.println("Clonagem solicitada com sucesso. Task ID: " + taskId);
            return taskId;

        } catch (Exception e) {
            // Logar o erro e lançar uma exceção de negócio
            System.err.println("Falha ao processar clonagem para " + vmName + ": " + e.getMessage());
            throw new RuntimeException("Falha na automação da infraestrutura: " + e.getMessage());
        }
    }
}