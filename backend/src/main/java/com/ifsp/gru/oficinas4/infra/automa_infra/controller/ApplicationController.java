package com.ifsp.gru.oficinas4.infra.automa_infra.controller;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Aplicações", description = "Endpoints para gerenciar aplicações")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // ------------------- READ ALL -------------------

    @GetMapping
    @Operation(summary = "Listar todas as aplicações",
            description = "Retorna lista paginada de todas as aplicações")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ApplicationResponseDTO>> getAllApplications(Pageable pageable) {
        Page<ApplicationResponseDTO> applications = applicationService.findAll(pageable);
        return ResponseEntity.ok(applications);
    }

    // ------------------- SEARCH BY NAME -------------------

    @GetMapping("/search")
    @Operation(summary = "Buscar aplicações por nome",
            description = "Busca aplicações cujo nome contenha o termo fornecido (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<Page<ApplicationResponseDTO>> searchApplicationByName(
            @RequestParam String name,
            Pageable pageable
    ) {
        Page<ApplicationResponseDTO> applications = applicationService.searchByName(name, pageable);
        return ResponseEntity.ok(applications);
    }

    // ------------------- SEARCH BY STATUS -------------------

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar aplicações por status",
            description = "Retorna todas as aplicações com um status específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ApplicationResponseDTO>> getApplicationsByStatus(
            @PathVariable String status,
            Pageable pageable
    ) {
        Page<ApplicationResponseDTO> applications = applicationService.findByStatus(status, pageable);
        return ResponseEntity.ok(applications);
    }

    // ------------------- SEARCH BY USER -------------------

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar aplicações por usuário",
            description = "Retorna todas as aplicações criadas por um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Page<ApplicationResponseDTO>> getApplicationsByUser(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        Page<ApplicationResponseDTO> applications = applicationService.findByUserId(userId, pageable);
        return ResponseEntity.ok(applications);
    }

    // ------------------- READ ONE -------------------

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aplicação por ID",
            description = "Retorna uma aplicação específica pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicação encontrada"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada")
    })
    public ResponseEntity<ApplicationResponseDTO> getApplicationById(@PathVariable Long id) {
        return applicationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- CREATE -------------------

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Criar nova aplicação",
            description = "Cria uma nova aplicação no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aplicação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApplicationResponseDTO> createApplication(@Valid @RequestBody ApplicationRequestDTO dto) {
        ApplicationResponseDTO created = applicationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------- UPDATE (PATCH) -------------------

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMON')")
    @Operation(summary = "Atualizar aplicação",
            description = "Atualiza parcialmente os dados de uma aplicação existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApplicationResponseDTO> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationPatchDTO dto
    ) {
        return applicationService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- UPDATE STATUS -------------------

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Atualizar status da aplicação",
            description = "Atualiza o status de uma aplicação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApplicationResponseDTO> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return applicationService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DEPLOY -------------------

    @PostMapping("/{id}/deploy")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMON')")
    @Operation(summary = "Realizar deploy da aplicação",
            description = "Executa o deploy de uma aplicação e atualiza a data de último deploy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deploy realizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApplicationResponseDTO> deployApplication(@PathVariable Long id) {
        return applicationService.deploy(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DELETE -------------------

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir aplicação",
            description = "Remove uma aplicação do sistema (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aplicação excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        boolean deleted = applicationService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ------------------- MÉTODOS AUXILIARES -------------------

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar se aplicação existe",
            description = "Verifica se existe uma aplicação com o ID fornecido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificação realizada")
    })
    public ResponseEntity<Boolean> applicationExists(@PathVariable Long id) {
        boolean exists = applicationService.exists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    @Operation(summary = "Contar total de aplicações",
            description = "Retorna o número total de aplicações cadastradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada")
    })
    public ResponseEntity<Long> countApplications() {
        long count = applicationService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Contar aplicações por status",
            description = "Retorna o número de aplicações com um status específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada")
    })
    public ResponseEntity<Long> countApplicationsByStatus(@PathVariable String status) {
        long count = applicationService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Contar aplicações por usuário",
            description = "Retorna o número de aplicações criadas por um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada")
    })
    public ResponseEntity<Long> countApplicationsByUser(@PathVariable Long userId) {
        long count = applicationService.countByUserId(userId);
        return ResponseEntity.ok(count);
    }
}