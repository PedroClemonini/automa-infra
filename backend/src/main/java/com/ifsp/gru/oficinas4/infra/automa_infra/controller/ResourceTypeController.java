package com.ifsp.gru.oficinas4.infra.automa_infra.controller;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ResourceTypeService;
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
@RequestMapping("/api/resource-types")
@Tag(name = "Tipos de Recursos", description = "Endpoints para gerenciar os tipos de recursos disponíveis")
public class ResourceTypeController {

    @Autowired
    private ResourceTypeService resourceTypeService;

    // ------------------- READ ALL -------------------

    @GetMapping
    @Operation(summary = "Listar todos os tipos de recurso",
            description = "Retorna lista paginada dos tipos de recursos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ResourceTypeResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(resourceTypeService.findAll(pageable));
    }

    // ------------------- SEARCH BY NAME -------------------

    @GetMapping("/search")
    @Operation(summary = "Buscar tipos de recurso por nome",
            description = "Busca tipos de recurso cujo nome contenha o termo fornecido (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<Page<ResourceTypeResponseDTO>> search(
            @RequestParam String name,
            Pageable pageable
    ) {
        return ResponseEntity.ok(resourceTypeService.searchByName(name, pageable));
    }

    // ------------------- READ ONE -------------------

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de recurso por ID",
            description = "Retorna um tipo de recurso específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de recurso encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de recurso não encontrado")
    })
    public ResponseEntity<ResourceTypeResponseDTO> getById(@PathVariable Long id) {
        return resourceTypeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- CREATE -------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo tipo de recurso",
            description = "Cria um novo tipo de recurso no sistema (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de recurso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<ResourceTypeResponseDTO> create(
            @Valid @RequestBody ResourceTypeRequestDTO dto
    ) {
        ResourceTypeResponseDTO created = resourceTypeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------- UPDATE (PATCH) -------------------

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar tipo de recurso",
            description = "Atualiza parcialmente os dados de um tipo de recurso existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado")
    })
    public ResponseEntity<ResourceTypeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ResourceTypePatchDTO dto
    ) {
        return resourceTypeService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DELETE -------------------

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir tipo de recurso",
            description = "Remove um tipo de recurso (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de recurso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de recurso não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = resourceTypeService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
