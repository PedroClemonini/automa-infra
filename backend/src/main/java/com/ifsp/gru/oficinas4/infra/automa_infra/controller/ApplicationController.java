package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@Tag(name = "Aplicações", description = "Endpoints para criacoes de VMs")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Operation(summary = "Lista todas as VMs", description = "Retorna uma lista com todas as VMs cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public List<Application> getAllApplications(){
        return  applicationRepository.findAll();
    };

    @Operation(summary = "Cria uma nova VM", description = "Cadastra uma nova VM no banco de dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VM criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: campos @NotBlank não preenchidos)")
    })
    @PostMapping
    public Application createApplication(@RequestBody Application contact) {
        return applicationRepository.save(contact);
    }
}
