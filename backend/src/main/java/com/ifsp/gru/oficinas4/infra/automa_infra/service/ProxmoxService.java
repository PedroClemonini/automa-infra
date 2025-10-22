package com.ifsp.gru.oficinas4.infra.automa_infra.service;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.CloneRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProxmoxService {

    private final WebClient webClient;

    // Injeção do WebClient configurado
    public ProxmoxService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Clona uma VM no Proxmox.
     * @param node O nome do nó Proxmox de origem.
     * @param vmid O ID da VM de origem (template).
     * @param cloneRequest O DTO com os parâmetros de clonagem (newid, name, full).
     * @return O ID da tarefa assíncrona do Proxmox (UPID).
     */
    public String cloneVm(String node, int vmid, CloneRequestDto cloneRequest) {
        String uri = String.format("/nodes/%s/qemu/%d/clone", node, vmid);

        return webClient.post()
                .uri(uri)
                .bodyValue(cloneRequest) // O Spring serializa o DTO para JSON
                .retrieve()
                .bodyToMono(ProxmoxResponse.class) // Proxmox retorna um JSON de resposta (com o UPID)
                .map(ProxmoxResponse::getData) // Assumindo que a resposta JSON tem a estrutura {"data": "UPID:..."}
                .block(); // Bloqueia para obter o resultado (pode ser trocado por Mono/Flux para reativo)
    }

    // Classe auxiliar para a resposta do Proxmox
    public static class ProxmoxResponse {
        private String data;
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}
