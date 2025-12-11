package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto.ApplicationRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto.ApplicationResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.mapper.ApplicationWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.usecase.*; // Importe todos os seus UseCases
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    // Injeção dos Use Cases
    private final CreateApplicationUseCase createUseCase;
    private final GetApplicationUseCase getUseCase;
    private final ListApplicationsUseCase listUseCase;
    private final UpdateApplicationUseCase updateUseCase;
    private final DeleteApplicationUseCase deleteUseCase;
    private final UpdateApplicationStatusUseCase updateStatusUseCase;
    private final DeployApplicationUseCase deployUseCase; // Assumindo este UseCase

    private final ApplicationWebMapper mapper;

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@RequestBody @Valid ApplicationRequest request) {
        // 1. Converte DTO -> Domain (não contém o User completo)
        Application domain = mapper.toDomain(request);

        // 2. Chama o Use Case, passando o ID do criador (para ser resolvido no Core)
        Application created = createUseCase.execute(domain, request.getCreatedById());

        // 3. Converte Domain -> Response
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    // --- READ ---
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        Application domain = getUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(domain));
    }

    // --- LIST ---
    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            Pageable pageable) {

        Page<Application> page = listUseCase.execute(name, status, userId, pageable);

        return ResponseEntity.ok(page.map(mapper::toResponse));
    }

    // --- UPDATE/PATCH (Dados Gerais) ---
    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(@PathVariable Long id, @RequestBody @Valid ApplicationRequest request) {
        // O Use Case de Patch espera um objeto Domain com os dados a serem atualizados
        Application domain = mapper.toDomain(request);

        Application updated = updateUseCase.execute(id, domain);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    // --- UPDATE STATUS (Endpoint específico, mais limpo) ---
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @RequestParam String newStatus) {
        Application updated = updateStatusUseCase.execute(id, newStatus);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    // --- DEPLOY ACTION (Exemplo de ação específica) ---
    @PostMapping("/{id}/deploy")
    public ResponseEntity<ApplicationResponse> deploy(@PathVariable Long id) {
        // Assumindo que o DeployUseCase retorna a Application atualizada
        Application deployed = deployUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(deployed));
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}