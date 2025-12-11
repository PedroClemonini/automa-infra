package com.ifsp.gru.oficinas4.infra.automa_infra.ApplicationController;

import com.ifsp.gru.oficinas4.infra.automa_infra.controller.ApplicationController;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerUnitTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private ApplicationResponseDTO responseDTO;
    private ApplicationRequestDTO requestDTO;
    private ApplicationPatchDTO patchDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ApplicationResponseDTO(
                1L,
                1L,
                "João Silva",
                "App Produção",
                "Aplicação de produção principal",
                "10.10.10.165",
                "ACTIVE",
                "admin",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestDTO = new ApplicationRequestDTO(
                1L,
                "App Produção",
                "Aplicação de produção principal",
                "ACTIVE",
                "admin",
                "senha123"
        );

        patchDTO = new ApplicationPatchDTO(
                "App Produção Atualizado",
                null,
                null,
                null,
                null
        );
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("Deve listar todas as aplicações com sucesso")
    void testGetAllApplications_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ApplicationResponseDTO> page = new PageImpl<>(List.of(responseDTO));

        when(applicationService.findAll(pageable)).thenReturn(page);

        var response = applicationController.getAllApplications(pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(applicationService).findAll(pageable);
    }

    // ==================== SEARCH BY NAME ====================

    @Test
    @DisplayName("Deve buscar aplicações por nome com sucesso")
    void testSearchApplicationByName_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ApplicationResponseDTO> page = new PageImpl<>(List.of(responseDTO));

        when(applicationService.searchByName("Produção", pageable))
                .thenReturn(page);

        var response = applicationController.searchApplicationByName("Produção", pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("App Produção", response.getBody().getContent().get(0).name());
        verify(applicationService).searchByName("Produção", pageable);
    }

    // ==================== SEARCH BY STATUS ====================

    @Test
    @DisplayName("Deve buscar aplicações por status com sucesso")
    void testGetApplicationsByStatus_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ApplicationResponseDTO> page = new PageImpl<>(List.of(responseDTO));

        when(applicationService.findByStatus("ACTIVE", pageable))
                .thenReturn(page);

        var response = applicationController.getApplicationsByStatus("ACTIVE", pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(applicationService).findByStatus("ACTIVE", pageable);
    }

    // ==================== SEARCH BY USER ====================

    @Test
    @DisplayName("Deve buscar aplicações por usuário com sucesso")
    void testGetApplicationsByUser_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ApplicationResponseDTO> page = new PageImpl<>(List.of(responseDTO));

        when(applicationService.findByUserId(1L, pageable))
                .thenReturn(page);

        var response = applicationController.getApplicationsByUser(1L, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(applicationService).findByUserId(1L, pageable);
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("Deve buscar aplicação por ID com sucesso")
    void testGetApplicationById_Success() {
        when(applicationService.findById(1L)).thenReturn(Optional.of(responseDTO));

        var response = applicationController.getApplicationById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("App Produção", response.getBody().name());
        verify(applicationService).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ID inexistente")
    void testGetApplicationById_NotFound() {
        when(applicationService.findById(999L)).thenReturn(Optional.empty());

        var response = applicationController.getApplicationById(999L);

        assertEquals(404, response.getStatusCode().value());
        verify(applicationService).findById(999L);
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("Deve criar aplicação com sucesso")
    void testCreateApplication_Success() {
        when(applicationService.create(requestDTO)).thenReturn(responseDTO);

        var response = applicationController.createApplication(requestDTO);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("App Produção", response.getBody().name());
        verify(applicationService).create(requestDTO);
    }

    // ==================== UPDATE (PATCH) ====================

    @Test
    @DisplayName("Deve atualizar aplicação com sucesso")
    void testUpdateApplication_Success() {
        when(applicationService.update(1L, patchDTO)).thenReturn(Optional.of(responseDTO));

        var response = applicationController.updateApplication(1L, patchDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("App Produção", response.getBody().name());
        verify(applicationService).update(1L, patchDTO);
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar aplicação inexistente")
    void testUpdateApplication_NotFound() {
        when(applicationService.update(999L, patchDTO)).thenReturn(Optional.empty());

        var response = applicationController.updateApplication(999L, patchDTO);

        assertEquals(404, response.getStatusCode().value());
        verify(applicationService).update(999L, patchDTO);
    }

    // ==================== UPDATE STATUS ====================

    @Test
    @DisplayName("Deve atualizar status da aplicação com sucesso")
    void testUpdateApplicationStatus_Success() {
        when(applicationService.updateStatus(1L, "INACTIVE"))
                .thenReturn(Optional.of(responseDTO));

        var response = applicationController.updateApplicationStatus(1L, "INACTIVE");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(applicationService).updateStatus(1L, "INACTIVE");
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar status de aplicação inexistente")
    void testUpdateApplicationStatus_NotFound() {
        when(applicationService.updateStatus(999L, "INACTIVE"))
                .thenReturn(Optional.empty());

        var response = applicationController.updateApplicationStatus(999L, "INACTIVE");

        assertEquals(404, response.getStatusCode().value());
        verify(applicationService).updateStatus(999L, "INACTIVE");
    }

    // ==================== DEPLOY ====================

    @Test
    @DisplayName("Deve realizar deploy da aplicação com sucesso")
    void testDeployApplication_Success() {
        when(applicationService.deploy(1L)).thenReturn(Optional.of(responseDTO));

        var response = applicationController.deployApplication(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(applicationService).deploy(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deploy de aplicação inexistente")
    void testDeployApplication_NotFound() {
        when(applicationService.deploy(999L)).thenReturn(Optional.empty());

        var response = applicationController.deployApplication(999L);

        assertEquals(404, response.getStatusCode().value());
        verify(applicationService).deploy(999L);
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("Deve deletar aplicação com sucesso")
    void testDeleteApplication_Success() {
        when(applicationService.delete(1L)).thenReturn(true);

        var response = applicationController.deleteApplication(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(applicationService).delete(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar aplicação inexistente")
    void testDeleteApplication_NotFound() {
        when(applicationService.delete(999L)).thenReturn(false);

        var response = applicationController.deleteApplication(999L);

        assertEquals(404, response.getStatusCode().value());
        verify(applicationService).delete(999L);
    }

    // ==================== EXISTS ====================

    @Test
    @DisplayName("Deve verificar se aplicação existe")
    void testApplicationExists_True() {
        when(applicationService.exists(1L)).thenReturn(true);

        var response = applicationController.applicationExists(1L);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody());
        verify(applicationService).exists(1L);
    }

    @Test
    @DisplayName("Deve verificar que aplicação não existe")
    void testApplicationExists_False() {
        when(applicationService.exists(999L)).thenReturn(false);

        var response = applicationController.applicationExists(999L);

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody());
        verify(applicationService).exists(999L);
    }

    // ==================== COUNT ====================

    @Test
    @DisplayName("Deve contar total de aplicações")
    void testCountApplications() {
        when(applicationService.count()).thenReturn(10L);

        var response = applicationController.countApplications();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10L, response.getBody());
        verify(applicationService).count();
    }

    @Test
    @DisplayName("Deve contar aplicações por status")
    void testCountApplicationsByStatus() {
        when(applicationService.countByStatus("ACTIVE")).thenReturn(7L);

        var response = applicationController.countApplicationsByStatus("ACTIVE");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(7L, response.getBody());
        verify(applicationService).countByStatus("ACTIVE");
    }

    @Test
    @DisplayName("Deve contar aplicações por usuário")
    void testCountApplicationsByUser() {
        when(applicationService.countByUserId(1L)).thenReturn(5L);

        var response = applicationController.countApplicationsByUser(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5L, response.getBody());
        verify(applicationService).countByUserId(1L);
    }
}