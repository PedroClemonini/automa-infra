package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto.ResourceRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto.ResourceResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.mapper.ResourceWebMapper;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resources.usecases.*; // Importe seus UseCases
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
    // Casos de Uso
    private final CreateResourceUseCase createUseCase;
    private final GetResourceUseCase getUseCase;
    private final ListResourcesUseCase listUseCase;
    private final UpdateResourceUseCase updateUseCase;
    private final DeleteResourceUseCase deleteUseCase;
    private final ToggleResourceActiveUseCase toggleActiveUseCase; // Se existir

    private final ResourceWebMapper mapper;

    @PostMapping
    public ResponseEntity<ResourceResponse> create(@RequestBody @Valid ResourceRequest request) {

        logger.info("Tentativa de criação de recurso recebida: {}", request.getName()); // Log de entrada

        try {
            // 1. Mapeamento
            Resource domain = mapper.toDomain(request);
            logger.debug("Requisição mapeada para o domínio: {}", domain);

            // 2. Execução do Caso de Uso
            Resource created = createUseCase.execute(domain);
            logger.info("Recurso criado com sucesso. ID: {}", created.getId());

            // 3. Mapeamento da Resposta
            ResourceResponse response = mapper.toResponse(created);

            // 4. Retorno
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Log de erro CRÍTICO com Stack Trace
            logger.error("Erro CRÍTICO ao processar a criação de recurso.", e);

            // Re-lança a exceção ou lança uma nova,
            // ou permite que o Exception Handler global do Spring lide com ela,
            // que é o que provavelmente está gerando seu 500 genérico.
            throw new RuntimeException("Falha ao criar recurso", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getById(@PathVariable Long id) {
        Resource domain = getUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(domain));
    }

    @GetMapping
    public ResponseEntity<Page<ResourceResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {

        Page<Resource> page = listUseCase.execute(name, typeId, active, pageable);

        return ResponseEntity.ok(page.map(mapper::toResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> update(@PathVariable Long id, @RequestBody @Valid ResourceRequest request) {

        Resource domain = mapper.toDomain(request);

        Resource updated = updateUseCase.execute(id, domain);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        toggleActiveUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}