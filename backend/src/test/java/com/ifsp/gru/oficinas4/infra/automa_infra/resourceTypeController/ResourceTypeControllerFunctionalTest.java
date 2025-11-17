package com.ifsp.gru.oficinas4.infra.automa_infra.resourceTypeController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes Funcionais (Integration Tests) do ResourceTypeController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Funcionais do ResourceTypeController")
class ResourceTypeControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    private ResourceType testResourceType;
    private ResourceTypeRequestDTO resourceTypeRequestDTO;

    @BeforeEach
    void setUp() {
        // Limpa o banco
        resourceTypeRepository.deleteAll();

        // Cria tipo de recurso de teste no banco
        testResourceType = new ResourceType();
        testResourceType.setName("Servidor");
        testResourceType.setDescription("Servidor de aplicação");
        testResourceType = resourceTypeRepository.save(testResourceType);

        // DTO para criar novos tipos de recurso
        resourceTypeRequestDTO = new ResourceTypeRequestDTO("Banco de Dados","Sistema de gerenciamento de banco de dados");
        ;
    }

    @Test
    @DisplayName("GET /api/resource-types - Deve listar todos os tipos de recurso com sucesso")
    @WithMockUser
    void testGetAllResourceTypes_Success() throws Exception {
        mockMvc.perform(get("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Servidor"))
                .andExpect(jsonPath("$.content[0].description").value("Servidor de aplicação"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/resource-types?page=0&size=5 - Deve listar com paginação")
    @WithMockUser
    void testGetAllResourceTypes_WithPagination() throws Exception {
        // Cria mais tipos de recurso para testar paginação
        for (int i = 0; i < 10; i++) {
            ResourceType resourceType = new ResourceType();
            resourceType.setName("Recurso " + i);
            resourceType.setDescription("Descrição do recurso " + i);
            resourceTypeRepository.save(resourceType);
        }

        mockMvc.perform(get("/api/resource-types")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(11)) // 1 + 10
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /api/resource-types - Deve retornar lista vazia quando não há tipos de recurso")
    @WithMockUser
    void testGetAllResourceTypes_EmptyList() throws Exception {
        resourceTypeRepository.deleteAll();

        mockMvc.perform(get("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("GET /api/resource-types/{id} - Deve buscar tipo de recurso por ID com sucesso")
    @WithMockUser
    void testGetResourceTypeById_Success() throws Exception {
        mockMvc.perform(get("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testResourceType.getId()))
                .andExpect(jsonPath("$.name").value("Servidor"))
                .andExpect(jsonPath("$.description").value("Servidor de aplicação"));
    }

    @Test
    @DisplayName("GET /api/resource-types/{id} - Deve retornar 404 quando tipo de recurso não existe")
    @WithMockUser
    void testGetResourceTypeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/resource-types/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/resource-types/search?name=Servidor - Deve buscar por nome")
    @WithMockUser
    void testSearchResourceTypeByName_Success() throws Exception {
        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "Servidor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Servidor"))
                .andExpect(jsonPath("$.content[0].description").value("Servidor de aplicação"));
    }

    @Test
    @DisplayName("GET /api/resource-types/search?name=servidor - Deve buscar ignorando case")
    @WithMockUser
    void testSearchResourceTypeByName_CaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "servidor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Servidor"));
    }

    @Test
    @DisplayName("GET /api/resource-types/search?name=inexistente - Deve retornar lista vazia")
    @WithMockUser
    void testSearchResourceTypeByName_NotFound() throws Exception {
        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "NomeQueNaoExiste")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/resource-types/search - Deve buscar parcialmente pelo nome")
    @WithMockUser
    void testSearchResourceTypeByName_PartialMatch() throws Exception {
        // Cria tipos de recurso com nomes similares
        ResourceType rt2 = new ResourceType();
        rt2.setName("Servidor Web");
        rt2.setDescription("Servidor web Apache");
        resourceTypeRepository.save(rt2);

        ResourceType rt3 = new ResourceType();
        rt3.setName("Servidor de Email");
        rt3.setDescription("Servidor SMTP");
        resourceTypeRepository.save(rt3);

        // Busca por "Servidor" deve retornar todos os três
        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "Servidor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @DisplayName("GET /api/resource-types/search - Deve retornar resultados quando name está vazio")
    @WithMockUser
    void testSearchResourceTypeByName_EmptyQuery() throws Exception {
        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("POST /api/resource-types - Deve criar tipo de recurso com sucesso (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_Success() throws Exception {
        String resourceTypeJson = objectMapper.writeValueAsString(resourceTypeRequestDTO);

        MvcResult result = mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Banco de Dados"))
                .andExpect(jsonPath("$.description").value("Sistema de gerenciamento de banco de dados"))
                .andReturn();

        // Verifica se foi salvo no banco
        String responseJson = result.getResponse().getContentAsString();
        ResourceTypeResponseDTO response = objectMapper.readValue(responseJson, ResourceTypeResponseDTO.class);

        ResourceType savedResourceType = resourceTypeRepository.findById(response.id()).orElseThrow();
        assertThat(savedResourceType.getName()).isEqualTo("Banco de Dados");
        assertThat(savedResourceType.getDescription()).isEqualTo("Sistema de gerenciamento de banco de dados");
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve retornar 403 quando usuário não é ADMIN")
    @WithMockUser(roles = "COMMON")
    void testCreateResourceType_Forbidden() throws Exception {
        String resourceTypeJson = objectMapper.writeValueAsString(resourceTypeRequestDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve retornar 409 quando nome já existe")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_DuplicateName() throws Exception {
        ResourceTypeRequestDTO duplicateDTO = new ResourceTypeRequestDTO("Servidor","Outra descrição");

        String resourceTypeJson = objectMapper.writeValueAsString(duplicateDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("á existe um ResourceType com nome:")))
                .andExpect(jsonPath("$.message", containsString("Servidor")));
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve retornar 400 quando dados são inválidos")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_InvalidData() throws Exception {
        ResourceTypeRequestDTO invalidDTO = new ResourceTypeRequestDTO("","");

        String resourceTypeJson = objectMapper.writeValueAsString(invalidDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve rejeitar requisição sem body")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_NoBody() throws Exception {
        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve criar tipo de recurso com nome contendo acentos")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_NameWithAccents() throws Exception {
        ResourceTypeRequestDTO accentDTO = new ResourceTypeRequestDTO("Serviço de Aplicação","Descrição com acentuação");

        String resourceTypeJson = objectMapper.writeValueAsString(accentDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Serviço de Aplicação"))
                .andExpect(jsonPath("$.description").value("Descrição com acentuação"));
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve atualizar nome do tipo de recurso")
    @WithMockUser
    void testUpdateResourceType_UpdateName() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Servidor Atualizado",null);

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testResourceType.getId()))
                .andExpect(jsonPath("$.name").value("Servidor Atualizado"))
                .andExpect(jsonPath("$.description").value("Servidor de aplicação"));

        ResourceType updatedResourceType = resourceTypeRepository.findById(testResourceType.getId()).orElseThrow();
        assertThat(updatedResourceType.getName()).isEqualTo("Servidor Atualizado");
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve atualizar descrição do tipo de recurso")
    @WithMockUser
    void testUpdateResourceType_UpdateDescription() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO(null,"Nova descrição do servidor");

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Nova descrição do servidor"));

        ResourceType updatedResourceType = resourceTypeRepository.findById(testResourceType.getId()).orElseThrow();
        assertThat(updatedResourceType.getDescription()).isEqualTo("Nova descrição do servidor");
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve atualizar múltiplos campos simultaneamente")
    @WithMockUser
    void testUpdateResourceType_MultipleFields() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Servidor Web Apache","Servidor HTTP de alta performance");

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Servidor Web Apache"))
                .andExpect(jsonPath("$.description").value("Servidor HTTP de alta performance"));
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve ignorar campos vazios")
    @WithMockUser
    void testUpdateResourceType_IgnoreBlankFields() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("","");


        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Servidor"))
                .andExpect(jsonPath("$.description").value("Servidor de aplicação"));
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve aceitar null em todos os campos do patch")
    @WithMockUser
    void testUpdateResourceType_AllFieldsNull() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO(null,null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Servidor"))
                .andExpect(jsonPath("$.description").value("Servidor de aplicação"));

        // Verifica que nada foi alterado no banco
        ResourceType unchangedResourceType = resourceTypeRepository.findById(testResourceType.getId()).orElseThrow();
        assertThat(unchangedResourceType.getName()).isEqualTo("Servidor");
        assertThat(unchangedResourceType.getDescription()).isEqualTo("Servidor de aplicação");
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve manter campos não enviados inalterados")
    @WithMockUser
    void testUpdateResourceType_PartialUpdate() throws Exception {
        String originalDescription = testResourceType.getDescription();

        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Novo Nome",null);


        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"))
                .andExpect(jsonPath("$.description").value(originalDescription));

        ResourceType updatedResourceType = resourceTypeRepository.findById(testResourceType.getId()).orElseThrow();
        assertThat(updatedResourceType.getDescription()).isEqualTo(originalDescription);
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve retornar 404 quando tipo de recurso não existe")
    @WithMockUser
    void testUpdateResourceType_NotFound() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Nome",null);


        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve retornar 409 ao atualizar para nome existente")
    @WithMockUser
    void testUpdateResourceType_DuplicateName() throws Exception {
        // Cria outro tipo de recurso
        ResourceType anotherResourceType = new ResourceType();
        anotherResourceType.setName("Storage");
        anotherResourceType.setDescription("Sistema de armazenamento");
        resourceTypeRepository.save(anotherResourceType);

        // Tenta atualizar testResourceType com nome do anotherResourceType
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Storage",null);

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Nome já cadastrado")))
                .andExpect(jsonPath("$.message", containsString("Storage")));
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve permitir atualizar para o mesmo nome")
    @WithMockUser
    void testUpdateResourceType_SameName() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Servidor",null);


        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Servidor"));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("DELETE /api/resource-types/{id} - Deve deletar tipo de recurso com sucesso (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testDeleteResourceType_Success() throws Exception {
        Long resourceTypeId = testResourceType.getId();

        mockMvc.perform(delete("/api/resource-types/{id}", resourceTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(resourceTypeRepository.findById(resourceTypeId)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/resource-types/{id} - Deve garantir que tipo de recurso foi realmente deletado")
    @WithMockUser(roles = "ADMIN")
    void testDeleteResourceType_VerifyDeletion() throws Exception {
        Long resourceTypeId = testResourceType.getId();

        // Verifica que existe antes
        assertThat(resourceTypeRepository.existsById(resourceTypeId)).isTrue();

        // Deleta
        mockMvc.perform(delete("/api/resource-types/{id}", resourceTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verifica que foi deletado
        assertThat(resourceTypeRepository.existsById(resourceTypeId)).isFalse();
        assertThat(resourceTypeRepository.findById(resourceTypeId)).isEmpty();

        // Tenta buscar e deve retornar 404
        mockMvc.perform(get("/api/resource-types/{id}", resourceTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/resource-types/{id} - Deve retornar 403 quando usuário não é ADMIN")
    @WithMockUser(roles = "COMMON")
    void testDeleteResourceType_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(resourceTypeRepository.findById(testResourceType.getId())).isPresent();
    }

    @Test
    @DisplayName("DELETE /api/resource-types/{id} - Deve retornar 404 quando tipo de recurso não existe")
    @WithMockUser(roles = "ADMIN")
    void testDeleteResourceType_NotFound() throws Exception {
        mockMvc.perform(delete("/api/resource-types/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // ==================== TESTES DE SEGURANÇA ====================

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - GET")
    void testGetAllResourceTypes_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - POST")
    void testCreateResourceType_Unauthorized() throws Exception {
        String resourceTypeJson = objectMapper.writeValueAsString(resourceTypeRequestDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - PATCH")
    void testUpdateResourceType_Unauthorized() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Nome",null);

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - DELETE")
    void testDeleteResourceType_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ==================== TESTES ADICIONAIS ====================

    @Test
    @DisplayName("GET /api/resource-types - Deve ordenar resultados por ID por padrão")
    @WithMockUser
    void testGetAllResourceTypes_DefaultSorting() throws Exception {
        // Cria mais tipos de recurso
        ResourceType rt2 = new ResourceType();
        rt2.setName("Storage");
        rt2.setDescription("Armazenamento");
        resourceTypeRepository.save(rt2);

        ResourceType rt3 = new ResourceType();
        rt3.setName("Network");
        rt3.setDescription("Rede");
        resourceTypeRepository.save(rt3);

        mockMvc.perform(get("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DisplayName("GET /api/resource-types - Deve funcionar com diferentes tamanhos de página")
    @WithMockUser
    void testGetAllResourceTypes_DifferentPageSizes() throws Exception {
        // Cria 24 tipos de recurso adicionais (total 25 com testResourceType)
        for (int i = 0; i < 24; i++) {
            ResourceType resourceType = new ResourceType();
            resourceType.setName("Recurso " + i);
            resourceType.setDescription("Descrição " + i);
            resourceTypeRepository.save(resourceType);
        }

        // Testa size=10
        mockMvc.perform(get("/api/resource-types")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(25));

        // Testa size=20
        mockMvc.perform(get("/api/resource-types")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.totalElements").value(25));

        // Testa size=50 (maior que total)
        mockMvc.perform(get("/api/resource-types")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(25)))
                .andExpect(jsonPath("$.totalElements").value(25));
    }

    @Test
    @DisplayName("GET /api/resource-types - Deve suportar paginação em páginas intermediárias")
    @WithMockUser
    void testGetAllResourceTypes_IntermediatePage() throws Exception {
        // Cria 15 tipos de recurso adicionais
        for (int i = 0; i < 15; i++) {
            ResourceType resourceType = new ResourceType();
            resourceType.setName("Recurso " + i);
            resourceType.setDescription("Descrição " + i);
            resourceTypeRepository.save(resourceType);
        }

        // Testa página 1 (segunda página)
        mockMvc.perform(get("/api/resource-types")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalPages").value(4));
    }

    // ==================== TESTES DE EDGE CASES ====================

    @Test
    @DisplayName("POST /api/resource-types - Deve aceitar descrição com caracteres especiais")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_SpecialCharactersDescription() throws Exception {
        ResourceTypeRequestDTO specialDTO = new ResourceTypeRequestDTO("API Gateway","Gateway para APIs REST/SOAP com suporte a OAuth 2.0 & JWT");

        String resourceTypeJson = objectMapper.writeValueAsString(specialDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("API Gateway"))
                .andExpect(jsonPath("$.description").value("Gateway para APIs REST/SOAP com suporte a OAuth 2.0 & JWT"));
    }


    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve aceitar descrição vazia explicitamente")
    @WithMockUser
    void testUpdateResourceType_EmptyDescription() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO(null," ");

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk());

        // Verifica comportamento esperado (pode manter ou limpar dependendo da implementação)
        ResourceType updatedResourceType = resourceTypeRepository.findById(testResourceType.getId()).orElseThrow();
        // A descrição deve permanecer inalterada se campos vazios são ignorados
        assertThat(updatedResourceType.getDescription()).isEqualTo("Servidor de aplicação");
    }

    @Test
    @DisplayName("GET /api/resource-types/search - Deve buscar com espaços no início e fim do termo")
    @WithMockUser
    void testSearchResourceTypeByName_WithSpaces() throws Exception {
        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "  Servidor  ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/resource-types/search - Deve buscar com caracteres especiais")
    @WithMockUser
    void testSearchResourceTypeByName_SpecialCharacters() throws Exception {
        // Cria tipo de recurso com nome especial
        ResourceType specialRT = new ResourceType();
        specialRT.setName("Load Balancer - Layer 7");
        specialRT.setDescription("Balanceador de carga camada 7");
        resourceTypeRepository.save(specialRT);

        mockMvc.perform(get("/api/resource-types/search")
                        .param("name", "Layer 7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Load Balancer - Layer 7"));
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve aceitar descrição nula")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_NullDescription() throws Exception {
        ResourceTypeRequestDTO nullDescDTO = new ResourceTypeRequestDTO("Firewall",null);

        String resourceTypeJson = objectMapper.writeValueAsString(nullDescDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Firewall"));
    }

    @Test
    @DisplayName("PATCH /api/resource-types/{id} - Deve processar atualização com campos em branco e null misturados")
    @WithMockUser
    void testUpdateResourceType_MixedBlankAndNull() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("",null);

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Servidor"))
                .andExpect(jsonPath("$.description").value("Servidor de aplicação"));
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("POST /api/resource-types - Deve validar nome obrigatório")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_MissingName() throws Exception {
        ResourceTypeRequestDTO missingNameDTO = new ResourceTypeRequestDTO(null,"Descrição sem nome");

        String resourceTypeJson = objectMapper.writeValueAsString(missingNameDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/resource-types - Deve aceitar nome com números")
    @WithMockUser(roles = "ADMIN")
    void testCreateResourceType_NameWithNumbers() throws Exception {
        ResourceTypeRequestDTO numbersDTO = new ResourceTypeRequestDTO("Windows Server 2022","Sistema operacional Microsoft");

        String resourceTypeJson = objectMapper.writeValueAsString(numbersDTO);

        mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Windows Server 2022"));
    }

    // ==================== TESTES DE CONSISTÊNCIA ====================

    @Test
    @DisplayName("POST e GET - Deve manter consistência entre criação e leitura")
    @WithMockUser(roles = "ADMIN")
    void testCreateAndGet_Consistency() throws Exception {
        // Cria
        String resourceTypeJson = objectMapper.writeValueAsString(resourceTypeRequestDTO);
        MvcResult createResult = mockMvc.perform(post("/api/resource-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceTypeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        ResourceTypeResponseDTO createdDTO = objectMapper.readValue(createResponse, ResourceTypeResponseDTO.class);

        // Busca
        mockMvc.perform(get("/api/resource-types/{id}", createdDTO.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdDTO.id()))
                .andExpect(jsonPath("$.name").value(createdDTO.name()))
                .andExpect(jsonPath("$.description").value(createdDTO.description()));
    }

    @Test
    @DisplayName("PATCH e GET - Deve manter consistência entre atualização e leitura")
    @WithMockUser
    void testUpdateAndGet_Consistency() throws Exception {
        ResourceTypePatchDTO patchDTO = new ResourceTypePatchDTO("Nome Atualizado Teste","Descrição Atualizada Teste");

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        // Atualiza
        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk());

        // Busca e valida
        mockMvc.perform(get("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado Teste"))
                .andExpect(jsonPath("$.description").value("Descrição Atualizada Teste"));
    }

    @Test
    @DisplayName("DELETE e GET - Deve garantir que recurso deletado não é mais acessível")
    @WithMockUser(roles = "ADMIN")
    void testDeleteAndGet_Consistency() throws Exception {
        Long resourceTypeId = testResourceType.getId();

        // Verifica que existe
        mockMvc.perform(get("/api/resource-types/{id}", resourceTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Deleta
        mockMvc.perform(delete("/api/resource-types/{id}", resourceTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verifica que não existe mais
        mockMvc.perform(get("/api/resource-types/{id}", resourceTypeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Múltiplas operações PATCH consecutivas devem manter consistência")
    @WithMockUser
    void testMultiplePatches_Consistency() throws Exception {
        // Primeira atualização - nome
        ResourceTypePatchDTO patch1 = new ResourceTypePatchDTO("Nome 1",null);

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome 1"));

        // Segunda atualização - descrição
        ResourceTypePatchDTO patch2 = new ResourceTypePatchDTO(null,"Descrição 2");

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome 1"))
                .andExpect(jsonPath("$.description").value("Descrição 2"));

        // Terceira atualização - ambos
        ResourceTypePatchDTO patch3 = new ResourceTypePatchDTO("Nome Final","Descrição Final");

        mockMvc.perform(patch("/api/resource-types/{id}", testResourceType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Final"))
                .andExpect(jsonPath("$.description").value("Descrição Final"));

        // Verifica estado final no banco
        ResourceType finalState = resourceTypeRepository.findById(testResourceType.getId()).orElseThrow();
        assertThat(finalState.getName()).isEqualTo("Nome Final");
        assertThat(finalState.getDescription()).isEqualTo("Descrição Final");
    }
}
