package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.service;

import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.client.ProxmoxClient;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxClone;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxCloneResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxNodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientService {
    private final ProxmoxClient client;

    public Mono<ProxmoxNodes> encontrarNodes(){
        return client.findAllNodes();
    }

    public Mono<ProxmoxCloneResponse> iniciarCloneVM(String node, int vmid, ProxmoxClone cloneParams){
        return client.cloneVM(node, vmid, cloneParams);
    }
}
