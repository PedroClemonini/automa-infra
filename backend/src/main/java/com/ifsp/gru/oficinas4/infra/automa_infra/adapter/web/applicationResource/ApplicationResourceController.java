package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.dto.ApplicationResourceRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.dto.ApplicationResourceResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.mapper.ApplicationResourceWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.usecases.*;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ApplicationResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/application-resources")
@RequiredArgsConstructor
public class ApplicationResourceController {

    // Use Cases de Vínculo
    private final CreateApplicationResourceUseCase createUseCase;
    private final GetApplicationResourceUseCase getUseCase;
    private final ListApplicationResourcesUseCase listUseCase;
    private final DeleteApplicationResourceUseCase deleteUseCase;
    private final UpdateApplicationResourceUseCase updateUseCase; // Para Patch de IDs

    private final ApplicationResourceWebMapper mapper;


    @PostMapping
    public ResponseEntity<ApplicationResourceResponse> create(@RequestBody @Valid ApplicationResourceRequest request) {
        // O Core espera os IDs para buscar as entidades e criar o vínculo
        ApplicationResource created = createUseCase.execute(request.getApplicationId(), request.getResourceId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }


    @GetMapping
    public ResponseEntity<Page<ApplicationResourceResponse>> list(
            @RequestParam(required = false) Long applicationId,
            Pageable pageable) {

        Page<ApplicationResource> page = listUseCase.execute(applicationId, pageable);

        return ResponseEntity.ok(page.map(mapper::toResponse));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResourceResponse> getById(@PathVariable Long id) {
        ApplicationResource domain = getUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(domain));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResourceResponse> update(@PathVariable Long id, @RequestBody @Valid ApplicationResourceRequest request) {
        // Converte DTO para Domain (contém IDs parciais para o patch)
        ApplicationResource incomingData = mapper.toDomain(request);

        ApplicationResource updated = updateUseCase.execute(id, incomingData);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}