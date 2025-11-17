package com.ifsp.gru.oficinas4.infra.automa_infra.userController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * Testes Funcionais (Integration Tests) do UserController
 * Refatorado para remover dependência do ModelMapper
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Funcionais do UserController")
class UserControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        // Limpa o banco
        userRepository.deleteAll();

        // Cria usuário de teste no banco
        testUser = new User();
        testUser.setName("João Silva");
        testUser.setEmail("joao@email.com");
        testUser.setPassword(passwordEncoder.encode("senha123"));
        testUser.setRole("COMMON");
        testUser = userRepository.save(testUser);

        // DTO para criar novos usuários
        userRequestDTO = new UserRequestDTO(
                "Maria Santos",
                "maria@email.com",
                "senha456",
                "COMMON"
        );
    }

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @DisplayName("GET /api/users - Deve listar todos os usuários com sucesso")
    @WithMockUser
    void testGetAllUsers_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("João Silva"))
                .andExpect(jsonPath("$.content[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$.content[0].role").value("COMMON"))
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/users?page=0&size=5 - Deve listar com paginação")
    @WithMockUser
    void testGetAllUsers_WithPagination() throws Exception {
        // Cria mais usuários para testar paginação
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setEmail("user" + i + "@email.com");
            user.setPassword(passwordEncoder.encode("senha"));
            user.setRole("COMMON");
            userRepository.save(user);
        }

        mockMvc.perform(get("/api/users")
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
    @DisplayName("GET /api/users - Deve retornar lista vazia quando não há usuários")
    @WithMockUser
    void testGetAllUsers_EmptyList() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("GET /api/users/{id} - Deve buscar usuário por ID com sucesso")
    @WithMockUser
    void testGetUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.role").value("COMMON"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Deve retornar 404 quando usuário não existe")
    @WithMockUser
    void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
}


    @Test
    @DisplayName("GET /api/users/search?name=João - Deve buscar por nome")
    @WithMockUser
    void testSearchUserByName_Success() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "João Silva")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("João Silva"))
                .andExpect(jsonPath("$.content[0].email").value("joao@email.com"));
    }

    @Test
    @DisplayName("GET /api/users/search?name=joão - Deve buscar ignorando case")
    @WithMockUser
    void testSearchUserByName_CaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "João Silva")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("João Silva"));
    }

    @Test
    @DisplayName("GET /api/users/search?name=inexistente - Deve retornar lista vazia")
    @WithMockUser
    void testSearchUserByName_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "NomeQueNaoExiste")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("POST /api/users - Deve criar usuário com sucesso (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_Success() throws Exception {
        String userJson = objectMapper.writeValueAsString(userRequestDTO);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Maria Santos"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.role").value("COMMON"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        // Verifica se foi salvo no banco com senha criptografada
        String responseJson = result.getResponse().getContentAsString();
        UserResponseDTO response = objectMapper.readValue(responseJson, UserResponseDTO.class);

        User savedUser = userRepository.findById(response.id()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("Maria Santos");
        assertThat(savedUser.getEmail()).isEqualTo("maria@email.com");
        assertThat(savedUser.getPassword()).isNotEqualTo("senha456");
        assertThat(passwordEncoder.matches("senha456", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /api/users - Deve criar usuário com role ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_WithAdminRole() throws Exception {
        UserRequestDTO adminDTO = new UserRequestDTO(
                "Admin User",
                "admin@email.com",
                "adminpass",
                "ADMIN"
        );
        String userJson = objectMapper.writeValueAsString(adminDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/users - Deve retornar 403 quando usuário não é ADMIN")
    @WithMockUser(roles = "COMMON")
    void testCreateUser_Forbidden() throws Exception {
        String userJson = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/users - Deve retornar 409 quando email já existe")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_DuplicateEmail() throws Exception {
        UserRequestDTO duplicateDTO = new UserRequestDTO(
                "Outro Nome",
                "joao@email.com", // Email já existe
                "senha789",
                "COMMON"
        );
        String userJson = objectMapper.writeValueAsString(duplicateDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email já cadastrado")))
                .andExpect(jsonPath("$.message", containsString("joao@email.com")));
    }

    @Test
    @DisplayName("POST /api/users - Deve retornar 409 quando username já existe")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_DuplicateUsername() throws Exception {
        UserRequestDTO duplicateDTO = new UserRequestDTO(
                "João Silva", // Nome já existe
                "outro@email.com",
                "senha789",
                "COMMON"
        );
        String userJson = objectMapper.writeValueAsString(duplicateDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Username já cadastrado")));
    }


    @Test
    @DisplayName("POST /api/users - Deve retornar 400 quando dados são inválidos")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_InvalidData() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO(
                "", // Nome vazio
                "email-invalido", // Email inválido
                "123", // Senha muito curta
                "COMMON"
        );
        String userJson = objectMapper.writeValueAsString(invalidDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve atualizar nome do usuário")
    @WithMockUser
    void testUpdateUser_UpdateName() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO("João Santos", null, null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value("João Santos"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("João Santos");
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve atualizar email do usuário")
    @WithMockUser
    void testUpdateUser_UpdateEmail() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO(null, "joao.novo@email.com", null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao.novo@email.com"));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("joao.novo@email.com");
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve atualizar apenas senha")
    @WithMockUser
    void testUpdateUser_OnlyPassword() throws Exception {
        String oldPasswordHash = testUser.getPassword();
        UserPatchDTO patchDTO = new UserPatchDTO(null, null, "novaSenha789", null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getPassword()).isNotEqualTo(oldPasswordHash);
        assertThat(updatedUser.getPassword()).isNotEqualTo("novaSenha789");
        assertThat(passwordEncoder.matches("novaSenha789", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve atualizar role do usuário")
    @WithMockUser
    void testUpdateUser_UpdateRole() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO(null, null, null, "ADMIN");
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve atualizar múltiplos campos simultaneamente")
    @WithMockUser
    void testUpdateUser_MultipleFields() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO(
                "João Carlos Santos",
                "joao.carlos@email.com",
                "novaSenha123",
                "ADMIN"
        );
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Carlos Santos"))
                .andExpect(jsonPath("$.email").value("joao.carlos@email.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve ignorar campos vazios")
    @WithMockUser
    void testUpdateUser_IgnoreBlankFields() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO("", "  ", "", null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve retornar 404 quando usuário não existe")
    @WithMockUser
    void testUpdateUser_NotFound() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO("Nome", null, null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve retornar 409 ao atualizar para email existente")
    @WithMockUser
    void testUpdateUser_DuplicateEmail() throws Exception {
        // Cria outro usuário
        User anotherUser = new User();
        anotherUser.setName("Pedro");
        anotherUser.setEmail("pedro@email.com");
        anotherUser.setPassword(passwordEncoder.encode("senha"));
        anotherUser.setRole("COMMON");
        anotherUser = userRepository.save(anotherUser);

        // Tenta atualizar testUser com email do anotherUser
        UserPatchDTO patchDTO = new UserPatchDTO(null, "pedro@email.com", null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Email já cadastrado")))
                .andExpect(jsonPath("$.message", containsString("pedro@email.com")));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve permitir atualizar para o mesmo email")
    @WithMockUser
    void testUpdateUser_SameEmail() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO(null, "joao@email.com", null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("DELETE /api/users/{id} - Deve deletar usuário com sucesso (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_Success() throws Exception {
        Long userId = testUser.getId();

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Deve retornar 403 quando usuário não é ADMIN")
    @WithMockUser(roles = "COMMON")
    void testDeleteUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(testUser.getId())).isPresent();
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Deve retornar 404 quando usuário não existe")
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // ==================== TESTES DE SEGURANÇA ====================

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - GET")
    void testGetAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - POST")
    void testCreateUser_Unauthorized() throws Exception {
        String userJson = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - PATCH")
    void testUpdateUser_Unauthorized() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO("Nome", null, null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 quando não está autenticado - DELETE")
    void testDeleteUser_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ==================== TESTES ADICIONAIS ====================

    @Test
    @DisplayName("GET /api/users/email/{email} - Deve buscar por email com sucesso")
    @WithMockUser
    void testGetUserByEmail_Success() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "joao@email.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.role").value("COMMON"));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Deve retornar 404 para email inexistente")
    @WithMockUser
    void testGetUserByEmail_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "inexistente@email.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/exists/{id} - Deve retornar true quando usuário existe")
    @WithMockUser
    void testUserExists_ReturnsTrue() throws Exception {
        mockMvc.perform(get("/api/users/exists/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/users/exists/{id} - Deve retornar false para usuário inexistente")
    @WithMockUser
    void testUserExists_ReturnsFalse() throws Exception {
        mockMvc.perform(get("/api/users/exists/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }


    // ==================== TESTES DE EDGE CASES ====================

    @Test
    @DisplayName("POST /api/users - Deve aceitar senhas com caracteres especiais")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_SpecialCharactersPassword() throws Exception {
        UserRequestDTO specialDTO = new UserRequestDTO(
                "User Especial",
                "especial@email.com",
                "S3nh@#Esp3ci@l!",
                "COMMON"
        );
        String userJson = objectMapper.writeValueAsString(specialDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("especial@email.com"));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve aceitar null em todos os campos do patch")
    @WithMockUser
    void testUpdateUser_AllFieldsNull() throws Exception {
        UserPatchDTO patchDTO = new UserPatchDTO(null, null, null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        // Verifica que nada foi alterado no banco
        User unchangedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(unchangedUser.getName()).isEqualTo("João Silva");
        assertThat(unchangedUser.getEmail()).isEqualTo("joao@email.com");
    }
    @Test
    @DisplayName("GET /api/users - Deve ordenar resultados por ID por padrão")
    @WithMockUser
    void testGetAllUsers_DefaultSorting() throws Exception {
        // Cria mais usuários
        User user2 = new User();
        user2.setName("Ana");
        user2.setEmail("ana@email.com");
        user2.setPassword(passwordEncoder.encode("senha"));
        user2.setRole("COMMON");
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("Carlos");
        user3.setEmail("carlos@email.com");
        user3.setPassword(passwordEncoder.encode("senha"));
        user3.setRole("COMMON");
        userRepository.save(user3);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DisplayName("POST /api/users - Deve validar formato de email")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_InvalidEmailFormat() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO(
                "Usuario Teste",
                "email-sem-arroba.com",
                "senha123",
                "COMMON"
        );
        String userJson = objectMapper.writeValueAsString(invalidDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users/search - Deve retornar resultados quando name está vazio")
    @WithMockUser
    void testSearchUserByName_EmptyQuery() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve manter campos não enviados inalterados")
    @WithMockUser
    void testUpdateUser_PartialUpdate() throws Exception {
        String originalEmail = testUser.getEmail();
        String originalPassword = testUser.getPassword();

        UserPatchDTO patchDTO = new UserPatchDTO("Novo Nome", null, null, null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"))
                .andExpect(jsonPath("$.email").value(originalEmail));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo(originalEmail);
        assertThat(updatedUser.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    @DisplayName("POST /api/users - Deve rejeitar requisição sem body")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_NoBody() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Deve criar usuário com nome contendo acentos")
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_NameWithAccents() throws Exception {
        UserRequestDTO accentDTO = new UserRequestDTO(
                "José da Silva Ração",
                "jose@email.com",
                "senha123",
                "COMMON"
        );
        String userJson = objectMapper.writeValueAsString(accentDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("José da Silva Ração"));
    }

    @Test
    @DisplayName("GET /api/users - Deve funcionar com diferentes tamanhos de página")
    @WithMockUser
    void testGetAllUsers_DifferentPageSizes() throws Exception {
        // Cria 24 usuários adicionais (total 25 com testUser)
        for (int i = 0; i < 24; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setEmail("user" + i + "@test.com");
            user.setPassword(passwordEncoder.encode("senha"));
            user.setRole("COMMON");
            userRepository.save(user);
        }

        // Testa size=10
        mockMvc.perform(get("/api/users")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(25));

        // Testa size=20
        mockMvc.perform(get("/api/users")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.totalElements").value(25));

        // Testa size=50 (maior que total)
        mockMvc.perform(get("/api/users")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(25)))
                .andExpect(jsonPath("$.totalElements").value(25));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Deve garantir que usuário foi realmente deletado")
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_VerifyDeletion() throws Exception {
        Long userId = testUser.getId();

        // Verifica que existe antes
        assertThat(userRepository.existsById(userId)).isTrue();

        // Deleta
        mockMvc.perform(delete("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verifica que foi deletado
        assertThat(userRepository.existsById(userId)).isFalse();
        assertThat(userRepository.findById(userId)).isEmpty();

        // Tenta buscar e deve retornar 404
        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Deve atualizar apenas password sem alterar outros campos")
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_OnlyPasswordVerification() throws Exception {
        String originalName = testUser.getName();
        String originalEmail = testUser.getEmail();
        String originalRole = testUser.getRole();

        UserPatchDTO patchDTO = new UserPatchDTO(null, null, "novasenha456", null);
        String patchJson = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo(originalName);
        assertThat(updatedUser.getEmail()).isEqualTo(originalEmail);
        assertThat(updatedUser.getRole()).isEqualTo(originalRole);
        assertThat(passwordEncoder.matches("novasenha456", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("GET /api/users/search - Deve buscar parcialmente pelo nome")
    @WithMockUser
    void testSearchUserByName_PartialMatch() throws Exception {
        // Cria usuários com nomes similares
        User user2 = new User();
        user2.setName("João Pedro");
        user2.setEmail("joao.pedro@email.com");
        user2.setPassword(passwordEncoder.encode("senha"));
        user2.setRole("COMMON");
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("João Carlos");
        user3.setEmail("joao.carlos@email.com");
        user3.setPassword(passwordEncoder.encode("senha"));
        user3.setRole("COMMON");
        userRepository.save(user3);

        // Busca por "João" deve retornar todos os três
        mockMvc.perform(get("/api/users/search")
                        .param("name", "João")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(3));
    }
}