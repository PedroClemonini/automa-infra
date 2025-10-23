package com.ifsp.gru.oficinas4.infra.automa_infra.service;

import com.ifsp.gru.oficinas4.infra.automa_infra.ProxmoxProperties; // 1. Importar
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.CloneRequestDto;
import org.springframework.http.HttpHeaders; // 2. Importar
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProxmoxService {

    private final WebClient webClient;
    private final ProxmoxProperties properties; // 3. Declarar o ProxmoxProperties

    // 4. Injetar o ProxmoxProperties no construtor
    public ProxmoxService(WebClient webClient, ProxmoxProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public String cloneVm(String node, int vmid, CloneRequestDto cloneRequest) {
        String uri = String.format("/nodes/%s/qemu/%d/clone", node, vmid);

        return webClient.post()
                .uri(uri)
                // 5. ADICIONAR O CABEÇALHO DE AUTENTICAÇÃO!
                .header(HttpHeaders.AUTHORIZATION, properties.getApiTokenHeader())
                .bodyValue(cloneRequest)
                .retrieve()
                .bodyToMono(ProxmoxResponse.class)
                .map(ProxmoxResponse::getData)
                .block();
    }

    // Classe auxiliar para a resposta do Proxmox
    public static class ProxmoxResponse {
        private String data;
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}