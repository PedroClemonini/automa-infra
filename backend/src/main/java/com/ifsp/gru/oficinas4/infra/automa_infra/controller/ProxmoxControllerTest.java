package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain.ProxmoxNodes;
import com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.service.WebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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


}
