package com.ifsp.gru.oficinas4.infra.automa_infra.controller;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource.ApplicationResourcePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource.ApplicationResourceRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.applicationResource.ApplicationResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ApplicationResourceService;
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
@RequestMapping("/api/application-resources")
@Tag(name = "Recursos de Aplicação", description = "Endpoints para vincular recursos (Resource) às aplicações")
public class ApplicationResourcesController {

    @Autowired
    private ApplicationResourceService applicationResourceService;

    // ------------------- READ ALL -------------------

    @GetMapping
    @Operation(summary = "Listar vínculos de aplicação e recurso",
            description = "Retorna lista paginada de todos os vínculos entre aplicações e recursos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ApplicationResourceResponseDTO>> getAll(Pageable pageable) {
        Page<ApplicationResourceResponseDTO> list = applicationResourceService.findAll(pageable);
        return ResponseEntity.ok(list);
    }



    // ------------------- READ ONE -------------------

    @GetMapping("/{id}")
    @Operation(summary = "Buscar vínculo por ID",
            description = "Retorna um vínculo específico entre aplicação e recurso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vínculo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vínculo não encontrado")
    })
    public ResponseEntity<ApplicationResourceResponseDTO> getById(@PathVariable Long id) {
        return applicationResourceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- CREATE -------------------

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Vincular recurso a aplicação",
            description = "Cria um vínculo entre uma aplicação e um recurso")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vínculo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Aplicação ou recurso não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApplicationResourceResponseDTO> create(
            @Valid @RequestBody ApplicationResourceRequestDTO dto
    ) {
        ApplicationResourceResponseDTO created = applicationResourceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------- UPDATE (PATCH) -------------------

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMON')")
    @Operation(summary = "Atualizar vínculo",
            description = "Atualiza parcialmente informações de um vínculo existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vínculo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Vínculo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ApplicationResourceResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationResourcePatchDTO dto
    ) {
        return applicationResourceService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DELETE -------------------

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desvincular recurso de aplicação",
            description = "Remove a ligação entre uma aplicação e um recurso (somente ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vínculo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vínculo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = applicationResourceService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ------------------- AUXILIAR -------------------

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar se vínculo existe",
            description = "Verifica se existe um vínculo entre aplicação e recurso com o ID fornecido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificação realizada")
    })
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean exists = applicationResourceService.exists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    @Operation(summary = "Contar vínculos",
            description = "Retorna o número total de vínculos aplicação-recurso cadastrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada")
    })
    public ResponseEntity<Long> count() {
        long count = applicationResourceService.count();
        return ResponseEntity.ok(count);
    }


}
