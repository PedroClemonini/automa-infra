package com.ifsp.gru.oficinas4.infra.automa_infra.ApplicationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.UserRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.terraform.TerraformFileBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // 👈 ADICIONE ESTA LINHA
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes Funcionais (Integration Tests) do ApplicationController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Funcionais do ApplicationController")
class ApplicationControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean // Use @MockBean para substituir o bean no contexto
    private TerraformFileBuilder terraformFileBuilder;

    private Application testApplication;
    private User testUser;
    private ApplicationRequestDTO applicationRequestDTO;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        userRepository.deleteAll();

        // Usuário
        testUser = new User();
        testUser.setName("João Silva");
        testUser.setEmail("joao@test.com");
        testUser.setPassword("senha123");
        testUser = userRepository.save(testUser);

        // Aplicação
        testApplication = new Application();
        testApplication.setName("App Produção");
        testApplication.setDescription("Aplicação principal");
        testApplication.setStatus("ACTIVE");
        testApplication.setSshUser("admin");
        testApplication.setSshPassword("senha123");
        testApplication.setCreatedAt(LocalDateTime.now());
        testApplication.setUpdatedAt(LocalDateTime.now());
        testApplication.setCreatedBy(testUser);
        testApplication = applicationRepository.save(testApplication);

        applicationRequestDTO = new ApplicationRequestDTO(
                testUser.getId(),
                "App Desenvolvimento",
                "Aplicação de dev",
                "INACTIVE",
                "dev",
                "devpass"
        );
    }

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @DisplayName("GET /api/applications - Deve listar todas as aplicações com sucesso")
    @WithMockUser
    void testGetAllApplications_Success() throws Exception {
        mockMvc.perform(get("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("App Produção"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/applications?page=0&size=5 - Deve listar com paginação")
    @WithMockUser(roles = "COMMON")
    void testGetAllApplications_WithPagination() throws Exception {

        for (int i = 0; i < 10; i++) {
            Application app = new Application();
            app.setCreatedBy(testUser);
            app.setName("App " + i);
            app.setDescription("Descrição " + i);
            app.setStatus("ACTIVE");
            app.setSshUser("user" + i);
            app.setSshPassword("pass" + i);
            app.setCreatedAt(LocalDateTime.now());
            app.setUpdatedAt(LocalDateTime.now());
            applicationRepository.save(app);
        }

        mockMvc.perform(get("/api/applications")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /api/applications - Deve retornar lista vazia quando não há aplicações")
    @WithMockUser
    void testGetAllApplications_EmptyList() throws Exception {
        applicationRepository.deleteAll();

        mockMvc.perform(get("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/applications/search?name=Produção - Deve buscar por nome")
    @WithMockUser
    void testSearchApplicationByName_Success() throws Exception {
        mockMvc.perform(get("/api/applications/search")
                        .param("name", "Produção")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("App Produção"));
    }

    @Test
    @DisplayName("GET /api/applications/search?name=produção - Deve buscar ignorando case")
    @WithMockUser
    void testSearchApplicationByName_CaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/applications/search")
                        .param("name", "produção")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("App Produção"));
    }
    @Test
    @DisplayName("GET /api/applications/search?name=inexistente - Deve retornar lista vazia")
    @WithMockUser
    void testSearchApplicationByName_NotFound() throws Exception {
        mockMvc.perform(get("/api/applications/search")
                        .param("name", "NomeQueNaoExiste")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/applications/search - Deve buscar parcialmente pelo nome")
    @WithMockUser
    void testSearchApplicationByName_PartialMatch() throws Exception {
        Application app2 = new Application();
        app2.setCreatedBy(testUser);
        app2.setName("App Produção Web");
        app2.setDescription("Web app");
        app2.setStatus("ACTIVE");
        app2.setSshUser("webuser");
        app2.setSshPassword("webpass");
        app2.setCreatedAt(LocalDateTime.now());
        app2.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app2);

        Application app3 = new Application();
        app3.setCreatedBy(testUser);
        app3.setName("App Produção API");
        app3.setDescription("API app");
        app3.setStatus("ACTIVE");
        app3.setSshUser("apiuser");
        app3.setSshPassword("apipass");
        app3.setCreatedAt(LocalDateTime.now());
        app3.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app3);

        mockMvc.perform(get("/api/applications/search")
                        .param("name", "Produção")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @DisplayName("GET /api/applications/status/ACTIVE - Deve buscar por status")
    @WithMockUser
    void testGetApplicationsByStatus_Success() throws Exception {
        mockMvc.perform(get("/api/applications/status/{status}", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }


    @Test
    @DisplayName("GET /api/applications/status/INACTIVE - Deve retornar lista vazia para status sem aplicações")
    @WithMockUser
    void testGetApplicationsByStatus_NoResults() throws Exception {
        mockMvc.perform(get("/api/applications/status/{status}", "INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/applications/user/{userId} - Deve buscar aplicações por usuário")
    @WithMockUser
    void testGetApplicationsByUser_Success() throws Exception {
        mockMvc.perform(get("/api/applications/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("GET /api/applications/user/999 - Deve retornar lista vazia para usuário sem aplicações")
    @WithMockUser
    void testGetApplicationsByUser_NoResults() throws Exception {
        mockMvc.perform(get("/api/applications/user/{userId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/applications/{id} - Deve buscar aplicação por ID com sucesso")
    @WithMockUser
    void testGetApplicationById_Success() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testApplication.getId()))
                .andExpect(jsonPath("$.name").value("App Produção"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/applications/{id} - Deve retornar 404 quando aplicação não existe")
    @WithMockUser
    void testGetApplicationById_NotFound() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/applications - Deve criar aplicação com sucesso")
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testCreateApplication_Success() throws Exception {
        String appJson = objectMapper.writeValueAsString(applicationRequestDTO);

        MvcResult result = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("App Desenvolvimento"))
                .andExpect(jsonPath("$.description").value("Aplicação de dev"))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.sshUser").value("dev"))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ApplicationResponseDTO response = objectMapper.readValue(responseJson, ApplicationResponseDTO.class);

        Application savedApp = applicationRepository.findById(response.id()).orElseThrow();
        assertThat(savedApp.getName()).isEqualTo("App Desenvolvimento");
        assertThat(savedApp.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("POST /api/applications - Deve retornar 403 quando usuário não tem permissão")
    @WithMockUser(roles = "GUEST")
    void testCreateApplication_Forbidden() throws Exception {
        String appJson = objectMapper.writeValueAsString(applicationRequestDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/applications - Deve retornar 400 quando dados são inválidos")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_InvalidData() throws Exception {
        ApplicationRequestDTO invalidDTO = new ApplicationRequestDTO(
                null, "", "", "", "", ""
        );

        String appJson = objectMapper.writeValueAsString(invalidDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/applications - Deve rejeitar requisição sem body")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_NoBody() throws Exception {
        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/applications - Deve retornar 404 quando usuário não existe")
    @WithMockUser(roles = "ADMIN")
    void testCreateApplication_UserNotFound() throws Exception {
        ApplicationRequestDTO invalidUserDTO = new ApplicationRequestDTO(
                9999L, "App Test", "Desc", "ACTIVE", "user", "pass"
        );

        String appJson = objectMapper.writeValueAsString(invalidUserDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/applications/{id} - Deve atualizar nome da aplicação")
    @WithMockUser(roles = "ADMIN")
    void testUpdateApplication_UpdateName() throws Exception {
        ApplicationPatchDTO patchDTO = new ApplicationPatchDTO(
                "Nome Atualizado", null, null, null, null
        );

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"));
    }

    @Test
    @DisplayName("DELETE e GET - Deve garantir que aplicação deletada não é mais acessível")
    @WithMockUser(roles = "ADMIN")
    void testDeleteAndGet_Consistency() throws Exception {
        Long appId = testApplication.getId();

        mockMvc.perform(get("/api/applications/{id}", appId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/applications/{id}", appId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/applications/{id}", appId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Múltiplas operações PATCH consecutivas devem manter consistência")
    @WithMockUser(roles = "COMMON")
    void testMultiplePatches_Consistency() throws Exception {
        ApplicationPatchDTO patch1 = new ApplicationPatchDTO("Nome 1", null, null, null, null);
        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome 1"));

        ApplicationPatchDTO patch2 = new ApplicationPatchDTO(null, "Descrição 2", null, null, null);
        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome 1"))
                .andExpect(jsonPath("$.description").value("Descrição 2"));

        ApplicationPatchDTO patch3 = new ApplicationPatchDTO("Nome Final", "Descrição Final", "MAINTENANCE", null, null);
        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Final"))
                .andExpect(jsonPath("$.description").value("Descrição Final"))
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));

        Application finalState = applicationRepository.findById(testApplication.getId()).orElseThrow();
        assertThat(finalState.getName()).isEqualTo("Nome Final");
        assertThat(finalState.getDescription()).isEqualTo("Descrição Final");
        assertThat(finalState.getStatus()).isEqualTo("MAINTENANCE");
    }
    @Test
    @DisplayName("POST /api/applications - Deve aceitar nome com caracteres especiais")
    @WithMockUser(roles = "ADMIN")
    void testCreateApplication_SpecialCharacters() throws Exception {
        ApplicationRequestDTO specialDTO = new ApplicationRequestDTO(
                testUser.getId(),
                "App-API_Gateway v2.0",
                "Gateway para APIs REST/SOAP com suporte a OAuth 2.0 & JWT",
                "ACTIVE",
                "api-user",
                "p@ssw0rd!"
        );

        String appJson = objectMapper.writeValueAsString(specialDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("App-API_Gateway v2.0"));
    }

    @Test
    @DisplayName("GET /api/applications/search - Deve buscar com espaços no início e fim")
    @WithMockUser
    void testSearchApplicationByName_WithSpaces() throws Exception {
        mockMvc.perform(get("/api/applications/search")
                        .param("name", "  Produção  ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/applications - Deve funcionar com diferentes tamanhos de página")
    @WithMockUser
    void testGetAllApplications_DifferentPageSizes() throws Exception {
        for (int i = 0; i < 24; i++) {
            Application app = new Application();
            app.setCreatedBy(testUser);
            app.setName("App " + i);
            app.setDescription("Desc " + i);
            app.setStatus("ACTIVE");
            app.setSshUser("user" + i);
            app.setSshPassword("pass" + i);
            app.setCreatedAt(LocalDateTime.now());
            app.setUpdatedAt(LocalDateTime.now());
            applicationRepository.save(app);
        }

        mockMvc.perform(get("/api/applications")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(25));

        mockMvc.perform(get("/api/applications")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.totalElements").value(25));

        mockMvc.perform(get("/api/applications")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(25)))
                .andExpect(jsonPath("$.totalElements").value(25));
    }

    @Test
    @DisplayName("GET /api/applications - Deve suportar paginação em páginas intermediárias")
    @WithMockUser
    void testGetAllApplications_IntermediatePage() throws Exception {
        for (int i = 0; i < 15; i++) {
            Application app = new Application();
            app.setCreatedBy(testUser);
            app.setName("App " + i);
            app.setDescription("Desc " + i);
            app.setStatus("ACTIVE");
            app.setSshUser("user" + i);
            app.setSshPassword("pass" + i);
            app.setCreatedAt(LocalDateTime.now());
            app.setUpdatedAt(LocalDateTime.now());
            applicationRepository.save(app);
        }

        mockMvc.perform(get("/api/applications")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalPages").value(4));
    }

    @Test
    @DisplayName("PATCH /api/applications/{id} - Deve ignorar campos vazios")
    @WithMockUser(roles = "COMMON")
    void testUpdateApplication_IgnoreBlankFields() throws Exception {
        ApplicationPatchDTO patchDTO = new ApplicationPatchDTO("", "", "", "", "");
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("App Produção"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/applications - Deve validar nome obrigatório")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_MissingName() throws Exception {
        ApplicationRequestDTO missingNameDTO = new ApplicationRequestDTO(
                testUser.getId(), null, "Desc", "ACTIVE", "user", "pass"
        );

        String appJson = objectMapper.writeValueAsString(missingNameDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/applications - Deve validar status obrigatório")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_MissingStatus() throws Exception {
        ApplicationRequestDTO missingStatusDTO = new ApplicationRequestDTO(
                testUser.getId(), "App Test", "Desc", null, "user", "pass"
        );

        String appJson = objectMapper.writeValueAsString(missingStatusDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/applications - Deve validar userId obrigatório")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_MissingUserId() throws Exception {
        ApplicationRequestDTO missingUserIdDTO = new ApplicationRequestDTO(
                null, "App Test", "Desc", "ACTIVE", "user", "pass"
        );

        String appJson = objectMapper.writeValueAsString(missingUserIdDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/applications - Deve validar sshUser obrigatório")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_MissingSshUser() throws Exception {
        ApplicationRequestDTO missingSshUserDTO = new ApplicationRequestDTO(
                testUser.getId(), "App Test", "Desc", "ACTIVE", null, "pass"
        );

        String appJson = objectMapper.writeValueAsString(missingSshUserDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/applications - Deve validar sshPassword obrigatório")
    @WithMockUser(roles = "COMMON")
    void testCreateApplication_MissingSshPassword() throws Exception {
        ApplicationRequestDTO missingSshPasswordDTO = new ApplicationRequestDTO(
                testUser.getId(), "App Test", "Desc", "ACTIVE", "user", null
        );

        String appJson = objectMapper.writeValueAsString(missingSshPasswordDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Deve filtrar aplicações corretamente por usuário")
    @WithMockUser
    void testGetApplicationsByUser_MultipleUsers() throws Exception {
        User user2 = new User();
        user2.setName("Maria Santos");
        user2.setEmail("maria@test.com");
        user2.setPassword("senha456");
        user2 = userRepository.save(user2);

        Application app2 = new Application();
        app2.setCreatedBy(user2);
        app2.setName("App User2");
        app2.setDescription("App do segundo usuário");
        app2.setStatus("ACTIVE");
        app2.setSshUser("maria");
        app2.setSshPassword("pass456");
        app2.setCreatedAt(LocalDateTime.now());
        app2.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app2);

        mockMvc.perform(get("/api/applications/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].userId").value(testUser.getId()));

        mockMvc.perform(get("/api/applications/user/{userId}", user2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].userId").value(user2.getId()));
    }
    @Test
    @DisplayName("Deve contar aplicações corretamente por diferentes status")
    @WithMockUser
    void testCountApplicationsByStatus_MultipleStatuses() throws Exception {
        Application app2 = new Application();
        app2.setCreatedBy(testUser);
        app2.setName("App Inactive");
        app2.setDescription("App inativa");
        app2.setStatus("INACTIVE");
        app2.setSshUser("user2");
        app2.setSshPassword("pass2");
        app2.setCreatedAt(LocalDateTime.now());
        app2.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app2);

        Application app3 = new Application();
        app3.setCreatedBy(testUser);
        app3.setName("App Maintenance");
        app3.setDescription("App em manutenção");
        app3.setStatus("MAINTENANCE");
        app3.setSshUser("user3");
        app3.setSshPassword("pass3");
        app3.setCreatedAt(LocalDateTime.now());
        app3.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app3);

        mockMvc.perform(get("/api/applications/count/status/{status}", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(get("/api/applications/count/status/{status}", "INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(get("/api/applications/count/status/{status}", "MAINTENANCE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Deploy deve atualizar lastDeployedAt sem alterar outros campos")
    @WithMockUser(roles = "COMMON")
    void testDeploy_OnlyUpdatesDeployDate() throws Exception {
        String originalName = testApplication.getName();
        String originalStatus = testApplication.getStatus();

        mockMvc.perform(post("/api/applications/{id}/deploy", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(originalName))
                .andExpect(jsonPath("$.status").value("DEPLOYED"))
                .andExpect(jsonPath("$.lastDeployedAt").exists());

        Application deployedApp = applicationRepository.findById(testApplication.getId()).orElseThrow();
        assertThat(deployedApp.getName()).isEqualTo(originalName);
        assertThat(deployedApp.getStatus()).isEqualTo("DEPLOYED");
        assertThat(deployedApp.getLastDeployedAt()).isNotNull();
    }
    @Test
    @DisplayName("PATCH /api/applications/{id} - Deve atualizar múltiplos campos")
    @WithMockUser(roles = "COMMON")
    void testUpdateApplication_MultipleFields() throws Exception {
        ApplicationPatchDTO patchDTO = new ApplicationPatchDTO(
                "Novo Nome", "Nova Descrição", "MAINTENANCE", null, null
        );

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"))
                .andExpect(jsonPath("$.description").value("Nova Descrição"))
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }

    @Test
    @DisplayName("PATCH /api/applications/{id} - Deve aceitar null em todos os campos")
    @WithMockUser(roles = "COMMON")
    void testUpdateApplication_AllFieldsNull() throws Exception {
        ApplicationPatchDTO patchDTO = new ApplicationPatchDTO(
                null, null, null, null, null
        );

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("App Produção"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Application unchangedApp = applicationRepository.findById(testApplication.getId()).orElseThrow();
        assertThat(unchangedApp.getName()).isEqualTo("App Produção");
    }

    @Test
    @DisplayName("PATCH /api/applications/{id} - Deve retornar 404 quando aplicação não existe")
    @WithMockUser(roles = {"COMMON"})
    void testUpdateApplication_NotFound() throws Exception {
        ApplicationPatchDTO patchDTO = new ApplicationPatchDTO(
                "Nome", null, null, null, null
        );

        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/applications/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DE ATUALIZAÇÃO DE STATUS ====================

    @Test
    @DisplayName("PATCH /api/applications/{id}/status - Deve atualizar status")
    @WithMockUser(roles = "ADMIN")
    void testUpdateApplicationStatus_Success() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        Application updatedApp = applicationRepository.findById(testApplication.getId()).orElseThrow();
        assertThat(updatedApp.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("PATCH /api/applications/{id}/status - Deve retornar 404 quando aplicação não existe")
    @WithMockUser(roles = "ADMIN")
    void testUpdateApplicationStatus_NotFound() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", 9999L)
                        .param("status", "INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DE DEPLOY ====================

    @Test
    @DisplayName("POST /api/applications/{id}/deploy - Deve realizar deploy com sucesso")
    @WithMockUser(roles = "COMMON")
    void testDeployApplication_Success() throws Exception {

        // Mock do output do Terraform
        String mockTerraformOutput = """
            {
              "vm_ip_addresses": {
                "value": {
                  "ubuntu_vm": ["192.168.1.100"]
                }
              }
            }
            """;

        // Configurar comportamento do mock
        when(terraformFileBuilder.buildAndApplyTerraformFile(
                any(Application.class),
                anyList()
        )).thenReturn(mockTerraformOutput);

        // Executar requisição
        mockMvc.perform(post("/api/applications/{id}/deploy", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testApplication.getId()))
                .andExpect(jsonPath("$.status").value("DEPLOYED"))
                .andExpect(jsonPath("$.lastDeployedAt").exists());


        Application deployedApp = applicationRepository
                .findById(testApplication.getId())
                .orElseThrow();

        assertThat(deployedApp.getLastDeployedAt()).isNotNull();
        assertThat(deployedApp.getStatus()).isEqualTo("DEPLOYED");
        assertThat(deployedApp.getUpdatedAt()).isNotNull();


        ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
        ArgumentCaptor<List> resourcesCaptor = ArgumentCaptor.forClass(List.class);

        verify(terraformFileBuilder, times(1))
                .buildAndApplyTerraformFile(appCaptor.capture(), resourcesCaptor.capture());

        // Validar argumentos capturados
        Application capturedApp = appCaptor.getValue();
        assertThat(capturedApp.getId()).isEqualTo(testApplication.getId());
        assertThat(capturedApp.getName()).isEqualTo("App Produção");
    }


    @Test
    @DisplayName("POST /api/applications/{id}/deploy - Deve retornar 404 quando aplicação não existe")
    @WithMockUser(roles = "COMMON")
    void testDeployApplication_NotFound() throws Exception {
        mockMvc.perform(post("/api/applications/{id}/deploy", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("DELETE /api/applications/{id} - Deve deletar aplicação com sucesso (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testDeleteApplication_Success() throws Exception {
        Long appId = testApplication.getId();

        mockMvc.perform(delete("/api/applications/{id}", appId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(applicationRepository.findById(appId)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/applications/{id} - Deve garantir que aplicação foi realmente deletada")
    @WithMockUser(roles = "ADMIN")
    void testDeleteApplication_VerifyDeletion() throws Exception {
        Long appId = testApplication.getId();

        assertThat(applicationRepository.existsById(appId)).isTrue();

        mockMvc.perform(delete("/api/applications/{id}", appId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(applicationRepository.existsById(appId)).isFalse();

        mockMvc.perform(get("/api/applications/{id}", appId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/applications/{id} - Deve retornar 403 quando usuário não é ADMIN")
    @WithMockUser(roles = "COMMON")
    void testDeleteApplication_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(applicationRepository.findById(testApplication.getId())).isPresent();
    }

    @Test
    @DisplayName("DELETE /api/applications/{id} - Deve retornar 404 quando aplicação não existe")
    @WithMockUser(roles = "ADMIN")
    void testDeleteApplication_NotFound() throws Exception {
        mockMvc.perform(delete("/api/applications/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DE MÉTODOS AUXILIARES ====================

    @Test
    @DisplayName("GET /api/applications/exists/{id} - Deve verificar se aplicação existe")
    @WithMockUser
    void testApplicationExists_True() throws Exception {
        mockMvc.perform(get("/api/applications/exists/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/applications/exists/{id} - Deve verificar que aplicação não existe")
    @WithMockUser
    void testApplicationExists_False() throws Exception {
        mockMvc.perform(get("/api/applications/exists/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("GET /api/applications/count - Deve contar total de aplicações")
    @WithMockUser
    void testCountApplications() throws Exception {
        mockMvc.perform(get("/api/applications/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("GET /api/applications/count/status/{status} - Deve contar por status")
    @WithMockUser
    void testCountApplicationsByStatus() throws Exception {
        mockMvc.perform(get("/api/applications/count/status/{status}", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("GET /api/applications/count/user/{userId} - Deve contar por usuário")
    @WithMockUser
    void testCountApplicationsByUser() throws Exception {
        mockMvc.perform(get("/api/applications/count/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    // ==================== TESTES DE SEGURANÇA ====================

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - GET")
    void testGetAllApplications_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - POST")
    void testCreateApplication_Unauthorized() throws Exception {
        String appJson = objectMapper.writeValueAsString(applicationRequestDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - PATCH")
    void testUpdateApplication_Unauthorized() throws Exception {
        ApplicationPatchDTO patchDTO = new ApplicationPatchDTO("Nome", null, null, null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - DELETE")
    void testDeleteApplication_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/applications/{id}", testApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ==================== TESTES DE CONSISTÊNCIA ====================

    @Test
    @DisplayName("POST e GET - Deve manter consistência entre criação e leitura")
    @WithMockUser(roles = "ADMIN")
    void testCreateAndGet_Consistency() throws Exception {
        String appJson = objectMapper.writeValueAsString(applicationRequestDTO);

        MvcResult createResult = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appJson))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        ApplicationResponseDTO createdDTO = objectMapper.readValue(createResponse, ApplicationResponseDTO.class);

        mockMvc.perform(get("/api/applications/{id}", createdDTO.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdDTO.id()))
                .andExpect(jsonPath("$.name").value(createdDTO.name()))
                .andExpect(jsonPath("$.status").value(createdDTO.status()));
    }
}