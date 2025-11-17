package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourcePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ResourceService;
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

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Recursos", description = "Endpoints para gerenciar recursos de infraestrutura")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    // ------------------- READ ALL -------------------

    @GetMapping
    @Operation(summary = "Listar todos os recursos",
            description = "Retorna lista paginada de todos os recursos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ResourceResponseDTO>> getAllResources(Pageable pageable) {
        Page<ResourceResponseDTO> resources = resourceService.findAll(pageable);
        return ResponseEntity.ok(resources);
    }

    // ------------------- SEARCH BY NAME -------------------

    @GetMapping("/search")
    @Operation(summary = "Buscar recursos por nome",
            description = "Busca recursos cujo nome contenha o termo fornecido (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<Page<ResourceResponseDTO>> searchResourceByName(
            @RequestParam String name,
            Pageable pageable
    ) {
        Page<ResourceResponseDTO> resources = resourceService.searchByName(name, pageable);
        return ResponseEntity.ok(resources);
    }

    // ------------------- SEARCH BY RESOURCE TYPE -------------------

    @GetMapping("/type/{resourceTypeId}")
    @Operation(summary = "Buscar recursos por tipo",
            description = "Retorna todos os recursos de um tipo específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de recurso não encontrado")
    })
    public ResponseEntity<Page<ResourceResponseDTO>> getResourcesByType(
            @PathVariable Long resourceTypeId,
            Pageable pageable
    ) {
        Page<ResourceResponseDTO> resources = resourceService.findByResourceTypeId(resourceTypeId, pageable);
        return ResponseEntity.ok(resources);
    }

    // ------------------- SEARCH BY ACTIVE STATUS -------------------

    @GetMapping("/active")
    @Operation(summary = "Listar recursos ativos",
            description = "Retorna lista paginada apenas de recursos ativos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ResourceResponseDTO>> getActiveResources(Pageable pageable) {
        Page<ResourceResponseDTO> resources = resourceService.findByActive(true, pageable);
        return ResponseEntity.ok(resources);
    }

    // ------------------- READ ONE -------------------

    @GetMapping("/{id}")
    @Operation(summary = "Buscar recurso por ID",
            description = "Retorna um recurso específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recurso encontrado"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<ResourceResponseDTO> getResourceById(@PathVariable Long id) {
        return resourceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // ------------------- CREATE -------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo recurso",
            description = "Cria um novo recurso no sistema (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Slug já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<ResourceResponseDTO> createResource(@Valid @RequestBody ResourceRequestDTO dto) {
        ResourceResponseDTO created = resourceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------- UPDATE (PATCH) -------------------

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar recurso",
            description = "Atualiza parcialmente os dados de um recurso existente (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Slug já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<ResourceResponseDTO> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourcePatchDTO dto
    ) {
        return resourceService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- TOGGLE ACTIVE STATUS -------------------

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alternar status ativo/inativo",
            description = "Alterna o status de ativo/inativo de um recurso (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<ResourceResponseDTO> toggleActiveStatus(@PathVariable Long id) {
        return resourceService.toggleActive(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DELETE -------------------

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir recurso",
            description = "Remove um recurso do sistema (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recurso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        boolean deleted = resourceService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ------------------- MÉTODOS AUXILIARES -------------------

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar se recurso existe",
            description = "Verifica se existe um recurso com o ID fornecido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificação realizada")
    })
    public ResponseEntity<Boolean> resourceExists(@PathVariable Long id) {
        boolean exists = resourceService.exists(id);
        return ResponseEntity.ok(exists);
    }


    @GetMapping("/count")
    @Operation(summary = "Contar total de recursos",
            description = "Retorna o número total de recursos cadastrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada")
    })
    public ResponseEntity<Long> countResources() {
        long count = resourceService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    @Operation(summary = "Contar recursos ativos",
            description = "Retorna o número de recursos ativos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada")
    })
    public ResponseEntity<Long> countActiveResources() {
        long count = resourceService.countByActive(true);
        return ResponseEntity.ok(count);
    }
}