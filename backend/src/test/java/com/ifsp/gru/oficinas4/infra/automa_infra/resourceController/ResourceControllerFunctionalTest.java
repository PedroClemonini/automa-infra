package com.ifsp.gru.oficinas4.infra.automa_infra.resourceController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Funcionais do UserController")
public class ResourceControllerFunctionalTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ResourceService resourceService;

    ResourceResponseDTO resourceResponse;

    @BeforeEach
    void setup() {
        resourceResponse = new ResourceResponseDTO(
                1L,
                10L,
                "Servidor",
                "Servidor Cloud",
                "Servidor para aplicações",
                "v1.0",
                "print('hello')",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    // --------------------------------------------------------------------------------------------
    // LISTAGEM
    // --------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/resources - Deve retornar página de recursos")
    void testGetAllResources() throws Exception {
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceResponse));
        Mockito.when(resourceService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }


    @Test
    @DisplayName("GET /api/resources/search - Deve buscar por nome")
    void testSearchResourceByName() throws Exception {
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceResponse));
        Mockito.when(resourceService.searchByName(eq("Servidor"), any())).thenReturn(page);

        mockMvc.perform(get("/api/resources/search")
                        .param("name", "Servidor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Servidor Cloud"));
    }


    @Test
    @DisplayName("GET /api/resources/type/{id} - Deve buscar por tipo")
    void testGetResourcesByType() throws Exception {
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceResponse));
        Mockito.when(resourceService.findByResourceTypeId(eq(10L), any())).thenReturn(page);

        mockMvc.perform(get("/api/resources/type/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].resourceTypeId").value(10L));
    }


    @Test
    @DisplayName("GET /api/resources/active - Deve listar ativos")
    void testGetActiveResources() throws Exception {
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(resourceResponse));
        Mockito.when(resourceService.findByActive(eq(true), any())).thenReturn(page);

        mockMvc.perform(get("/api/resources/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].active").value(true));
    }


    // --------------------------------------------------------------------------------------------
    // GET POR ID
    // --------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/resources/{id} - Deve retornar recurso existente")
    void testGetResourceById_Found() throws Exception {
        Mockito.when(resourceService.findById(1L)).thenReturn(Optional.of(resourceResponse));

        mockMvc.perform(get("/api/resources/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }


    @Test
    @DisplayName("GET /api/resources/{id} - Deve retornar 404 quando não encontrado")
    void testGetResourceById_NotFound() throws Exception {
        Mockito.when(resourceService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/resources/999"))
                .andExpect(status().isNotFound());
    }


    // --------------------------------------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/resources - Deve criar recurso")
    void testCreateResource() throws Exception {
        ResourceRequestDTO request = new ResourceRequestDTO(
                10L, "Servidor X", "Desc", "1.0", "code", true
        );

        Mockito.when(resourceService.create(any())).thenReturn(resourceResponse);

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }


    @Test
    @DisplayName("POST /api/resources - Deve retornar 401 sem autenticação")
    void testCreateResource_Unauthenticated() throws Exception {
        ResourceRequestDTO request = new ResourceRequestDTO(
                10L, "Servidor X", "Desc", "1.0", "code", true
        );

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    // --------------------------------------------------------------------------------------------
    // PATCH UPDATE
    // --------------------------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/resources/{id} - Deve atualizar recurso")
    void testPatchUpdateResource() throws Exception {
        ResourcePatchDTO patchDTO = new ResourcePatchDTO(
                null, "Nome Atualizado", null, null, null, null
        );

        Mockito.when(resourceService.update(eq(1L), any()))
                .thenReturn(Optional.of(resourceResponse));

        mockMvc.perform(patch("/api/resources/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/resources/{id} - Deve retornar 404 quando não encontrado")
    void testPatchUpdateResource_NotFound() throws Exception {
        ResourcePatchDTO patchDTO = new ResourcePatchDTO(
                null, "Teste", null, null, null, null
        );

        Mockito.when(resourceService.update(eq(999L), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/resources/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PATCH /api/resources/{id} - Deve retornar 401 sem autenticação")
    void testPatchUpdateResource_Unauthenticated() throws Exception {
        ResourcePatchDTO patchDTO = new ResourcePatchDTO(
                null, "Teste", null, null, null, null
        );

        mockMvc.perform(patch("/api/resources/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isUnauthorized());
    }


    // --------------------------------------------------------------------------------------------
    // TOGGLE ACTIVE
    // --------------------------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/resources/{id}/toggle-active - Alterna status")
    void testToggleActive() throws Exception {
        Mockito.when(resourceService.toggleActive(1L))
                .thenReturn(Optional.of(resourceResponse));

        mockMvc.perform(patch("/api/resources/{id}/toggle-active", 1L))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("PATCH /api/resources/{id}/toggle-active - 401 sem login")
    void testToggleActive_Unauth() throws Exception {
        mockMvc.perform(patch("/api/resources/1/toggle-active"))
                .andExpect(status().isUnauthorized());
    }


    // --------------------------------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/resources/{id} - Deve excluir")
    void testDeleteResource() throws Exception {
        Mockito.when(resourceService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/resources/{id}", 1L))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/resources/{id} - Deve retornar 404")
    void testDeleteResource_NotFound() throws Exception {
        Mockito.when(resourceService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/resources/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("DELETE /api/resources/{id} - 401 sem autenticação")
    void testDeleteResource_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isUnauthorized());
    }


    // --------------------------------------------------------------------------------------------
    // COUNT E EXISTS
    // --------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/resources/exists/{id} - Deve verificar existência")
    void testExists() throws Exception {
        Mockito.when(resourceService.exists(1L)).thenReturn(true);

        mockMvc.perform(get("/api/resources/exists/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


    @Test
    @DisplayName("GET /api/resources/count - Deve retornar contagem")
    void testCountResources() throws Exception {
        Mockito.when(resourceService.count()).thenReturn(5L);

        mockMvc.perform(get("/api/resources/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }


    @Test
    @DisplayName("GET /api/resources/count/active - Deve contar ativos")
    void testCountActive() throws Exception {
        Mockito.when(resourceService.countByActive(true)).thenReturn(3L);

        mockMvc.perform(get("/api/resources/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

}
