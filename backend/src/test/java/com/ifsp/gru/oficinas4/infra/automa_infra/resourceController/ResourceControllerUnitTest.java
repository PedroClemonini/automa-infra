package com.ifsp.gru.oficinas4.infra.automa_infra.resourceController;

import com.ifsp.gru.oficinas4.infra.automa_infra.controller.ResourceController;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourcePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceControllerUnitTest {

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private ResourceResponseDTO resourceDTO;
    private ResourceRequestDTO requestDTO;
    private ResourcePatchDTO patchDTO;

    @BeforeEach
    void setUp() {

        resourceDTO = new ResourceResponseDTO(
                1L,
                1L,
                "Tipo Teste",
                "Servidor Dell",
                "Descrição teste",
                "v1",
                "print('Olá')",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestDTO = new ResourceRequestDTO(
                1L,
                "Servidor Dell",
                "Descrição teste",
                "v1",
                "print('Olá')",
                true
        );

        patchDTO = new ResourcePatchDTO(
                1L,          // resourceTypeId
                "Servidor Patch",
                null,
                null,
                null,
                null
        );
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("Deve listar todos os recursos com sucesso")
    void testGetAllResources_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceDTO));

        when(resourceService.findAll(pageable)).thenReturn(page);

        var response = resourceController.getAllResources(pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(resourceService).findAll(pageable);
    }

    // ==================== SEARCH NAME ====================

    @Test
    @DisplayName("Deve buscar recursos por nome com sucesso")
    void testSearchResourceByName_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceDTO));

        when(resourceService.searchByName("Servidor", pageable))
                .thenReturn(page);

        var response = resourceController.searchResourceByName("Servidor", pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Servidor Dell", response.getBody().getContent().get(0).name());
    }

    // ==================== SEARCH BY TYPE ====================

    @Test
    @DisplayName("Deve buscar recursos por tipo com sucesso")
    void testGetResourcesByType_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceDTO));

        when(resourceService.findByResourceTypeId(1L, pageable))
                .thenReturn(page);

        var response = resourceController.getResourcesByType(1L, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(resourceService).findByResourceTypeId(1L, pageable);
    }

    // ==================== SEARCH ACTIVE ====================

    @Test
    @DisplayName("Deve listar recursos ativos")
    void testGetActiveResources_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceDTO));

        when(resourceService.findByActive(true, pageable)).thenReturn(page);

        var response = resourceController.getActiveResources(pageable);

        assertEquals(200, response.getStatusCode().value());
        verify(resourceService).findByActive(true, pageable);
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("Deve buscar recurso por ID com sucesso")
    void testGetResourceById_Success() {
        when(resourceService.findById(1L)).thenReturn(Optional.of(resourceDTO));

        var response = resourceController.getResourceById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Servidor Dell", response.getBody().name());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ID inexistente")
    void testGetResourceById_NotFound() {
        when(resourceService.findById(999L)).thenReturn(Optional.empty());

        var response = resourceController.getResourceById(999L);

        assertEquals(404, response.getStatusCode().value());
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("Deve criar recurso com sucesso")
    void testCreateResource_Success() {
        when(resourceService.create(requestDTO)).thenReturn(resourceDTO);

        var response = resourceController.createResource(requestDTO);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Servidor Dell", response.getBody().name());
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("Deve atualizar recurso com sucesso")
    void testUpdateResource_Success() {
        when(resourceService.update(1L, patchDTO)).thenReturn(Optional.of(resourceDTO));

        var response = resourceController.updateResource(1L, patchDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Servidor Dell", response.getBody().name());
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar recurso inexistente")
    void testUpdateResource_NotFound() {
        when(resourceService.update(999L, patchDTO)).thenReturn(Optional.empty());

        var response = resourceController.updateResource(999L, patchDTO);

        assertEquals(404, response.getStatusCode().value());
    }

    // ==================== TOGGLE ACTIVE ====================

    @Test
    @DisplayName("Deve alternar active com sucesso")
    void testToggleActive_Success() {
        when(resourceService.toggleActive(1L)).thenReturn(Optional.of(resourceDTO));

        var response = resourceController.toggleActiveStatus(1L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar alternar active de recurso inexistente")
    void testToggleActive_NotFound() {
        when(resourceService.toggleActive(999L)).thenReturn(Optional.empty());

        var response = resourceController.toggleActiveStatus(999L);

        assertEquals(404, response.getStatusCode().value());
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("Deve deletar recurso com sucesso")
    void testDeleteResource_Success() {
        when(resourceService.delete(1L)).thenReturn(true);

        var response = resourceController.deleteResource(1L);

        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar recurso inexistente")
    void testDeleteResource_NotFound() {
        when(resourceService.delete(999L)).thenReturn(false);

        var response = resourceController.deleteResource(999L);

        assertEquals(404, response.getStatusCode().value());
    }

    // ==================== EXISTS ====================

    @Test
    @DisplayName("Deve verificar se recurso existe")
    void testResourceExists() {
        when(resourceService.exists(1L)).thenReturn(true);

        var response = resourceController.resourceExists(1L);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody());
    }

    // ==================== COUNT ====================

    @Test
    @DisplayName("Deve contar recursos")
    void testCountResources() {
        when(resourceService.count()).thenReturn(5L);

        var response = resourceController.countResources();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5L, response.getBody());
    }

    @Test
    @DisplayName("Deve contar recursos ativos")
    void testCountActiveResources() {
        when(resourceService.countByActive(true)).thenReturn(3L);

        var response = resourceController.countActiveResources();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3L, response.getBody());
    }

}
