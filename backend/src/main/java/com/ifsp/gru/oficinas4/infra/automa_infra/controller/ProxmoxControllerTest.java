package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxClone;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxCloneResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxNodes;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.service.WebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/proxmox")
@RequiredArgsConstructor
public class ProxmoxControllerTest {

    private final WebClientService service;

    @GetMapping
    public Mono<ProxmoxNodes> consultarPersonagemPorId() {
        return service.encontrarNodes();
    }
    @PostMapping
    public Mono<ProxmoxCloneResponse> createvm(@RequestBody ProxmoxClone clone){
        return service.iniciarCloneVM("srv60",103,clone);
    }


}
