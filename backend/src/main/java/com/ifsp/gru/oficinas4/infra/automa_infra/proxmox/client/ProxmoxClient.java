package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.client;

import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxNodes;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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

}
