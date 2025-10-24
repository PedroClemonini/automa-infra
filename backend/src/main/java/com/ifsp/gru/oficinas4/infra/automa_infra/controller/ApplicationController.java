package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/application")
@Tag(name = "Aplicações", description = "Endpoints para criacoes de VMs")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRepository applicationRepository;

    @Operation(summary = "Lista todas as VMs", description = "Retorna uma lista com todas as VMs cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public Flux<Application> getAllApplications() {
        // Converte a lista síncrona do JPA para Flux
        return Flux.fromIterable(applicationRepository.findAll());
    }

    @Operation(summary = "Cria uma nova VM", description = "Cadastra uma nova VM no banco de dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VM criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: campos @NotBlank não preenchidos)")
    })
    @PostMapping
    public Mono<Application> createApplication(@RequestBody Application application) {
        // Converte o retorno síncrono do JPA para Mono
        return Mono.just(applicationRepository.save(application));
    }
}
