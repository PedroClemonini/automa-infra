package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.client;

import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxClone;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxCloneResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxNodes;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.http.MediaType.APPLICATION_JSON;
@Component
@AllArgsConstructor
public class ProxmoxClient {
    private final WebClient webClient;

    public Mono<ProxmoxNodes> findAllNodes() {

        var proxmoxNodes = webClient
                .get()
                .uri("/nodes/")
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("Favor informar um ID válido.")))
                .bodyToMono(ProxmoxNodes.class);

        return proxmoxNodes;
    }

    /**
     * Inicia a operação de clonagem de uma VM no Proxmox.
     *
     * @param node O nó onde a VM original está localizada (Ex: "pve").
     * @param vmid O ID da VM original a ser clonada (Ex: 100).
     * @param cloneParams O corpo da requisição contendo os parâmetros do clone (Ex: newid=200).
     * @return Mono contendo o ProxmoxTaskResponse, que tem o UPID da tarefa.
     */
    public Mono<ProxmoxCloneResponse> cloneVM(String node, int vmid, ProxmoxClone cloneParams) {

        // 1. Define o URI com as variáveis de path
        String uri = String.format("/nodes/%s/qemu/%d/clone", node, vmid);

        return webClient
                .post()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .body(Mono.just(cloneParams), ProxmoxClone.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("Falha ao clonar VM. Erro Proxmox: " + error.statusCode())))
                .bodyToMono(ProxmoxCloneResponse.class)
                .onErrorResume(e -> {
                    // Log e re-lança um erro mais amigável, se necessário
                    System.err.println("Erro ao chamar API de clone: " + e.getMessage());
                    return Mono.error(new RuntimeException("Não foi possível iniciar a clonagem."));
                });
    }

}
