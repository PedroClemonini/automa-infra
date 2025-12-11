package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.dto.ResourceTypeRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.dto.ResourceTypeResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resourceType.mapper.ResourceTypeWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.resourceTypes.usecases.*; // Importe seus UseCases aqui
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource-types")
@RequiredArgsConstructor
public class ResourceTypeController {

    private final CreateResourceTypeUseCase createUseCase;
    private final UpdateResourceTypeUseCase updateUseCase;
    private final GetResourceTypeUseCase getUseCase;
    private final ListResourceTypesUseCase listUseCase;
    private final DeleteResourceTypeUseCase deleteUseCase;

    private final ResourceTypeWebMapper mapper;

    @PostMapping
    public ResponseEntity<ResourceTypeResponse> create(@RequestBody @Valid ResourceTypeRequest request) {
        ResourceType domain = mapper.toDomain(request);

        ResourceType created = createUseCase.execute(domain);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceTypeResponse> getById(@PathVariable Long id) {
        ResourceType domain = getUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(domain));
    }

    @GetMapping
    public ResponseEntity<Page<ResourceTypeResponse>> list(
            @RequestParam(required = false) String name,
            Pageable pageable) {

        // Assumindo que o ListUseCase aceita (nome, pageable) ou apenas (pageable)
        Page<ResourceType> page = listUseCase.execute(name, pageable);

        // Converte a Page de Domain para Page de Response
        return ResponseEntity.ok(page.map(mapper::toResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceTypeResponse> update(@PathVariable Long id, @RequestBody @Valid ResourceTypeRequest request) {
        ResourceType domain = mapper.toDomain(request);
        domain.setId(id); // Garante que o ID da URL seja usado

        ResourceType updated = updateUseCase.execute(id,domain);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}