package com.ifsp.gru.oficinas4.infra.automa_infra.resourceController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resource.ResourceResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Funcionais do ResourceController")
public class ResourceControllerFunctionalTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ResourceTypeRepository resourceTypeRepository;

    Resource languageResource;
    Resource dbResource;
    ResourceType languageType;
    ResourceType databaseType;
    ResourceResponseDTO resourceResponse;

    @BeforeEach
    void setup() {
        resourceRepository.deleteAll();
        resourceTypeRepository.deleteAll();

        languageType = new ResourceType();
        languageType.setName("Language");
        languageType.setDescription("Linguagem de programação");
        languageType = resourceTypeRepository.save(languageType);

        databaseType = new ResourceType();
        databaseType.setName("Database");
        databaseType.setDescription("Banco de Dados");
        databaseType = resourceTypeRepository.save(databaseType);

        languageResource = new Resource();
        languageResource.setResourceType(languageType);
        languageResource.setName("Java 17");
        languageResource.setDescription("Java Development Kit versão 17");
        languageResource.setVersion("17.0.5");
        languageResource.setActive(true);
        languageResource.setCodeSnippet(Arrays.asList(
                "sudo apt-get update -y",
                "sudo apt-get install -y fontconfig openjdk-17-jre",
                "java -version"
        ));
        languageResource.setCreatedAt(LocalDateTime.now());
        languageResource.setUpdatedAt(LocalDateTime.now());
        languageResource = resourceRepository.save(languageResource);

        dbResource = new Resource();
        dbResource.setResourceType(databaseType);
        dbResource.setName("PostgreSql 15");
        dbResource.setDescription("PGSQL versão 15");
        dbResource.setVersion("15.0.0");
        dbResource.setActive(true);
        dbResource.setCodeSnippet(Arrays.asList(
                "sudo apt-get update -y",
                "sudo apt-get install -y curl ca-certificates gnupg",
                "sudo install -m 0755 -d /etc/apt/keyrings",
                "curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo gpg --dearmor -o /etc/apt/keyrings/postgres.gpg",
                "echo \"deb [signed-by=/etc/apt/keyrings/postgres.gpg] http://apt.postgresql.org/pub/repos/apt/ noble-pgdg main\" | sudo tee /etc/apt/sources.list.d/postgresql.list",
                "sudo apt-get update -y",
                "sudo apt-get install -y postgresql-15",
                "sudo systemctl enable postgresql",
                "sudo systemctl start postgresql"
        ));
        dbResource.setCreatedAt(LocalDateTime.now());
        dbResource.setUpdatedAt(LocalDateTime.now());
        dbResource = resourceRepository.save(dbResource);
    }

    @Test
    @DisplayName("GET /api/resources - Deve listar todos os recursos com sucesso")
    @WithMockUser
    void testGetAllResources_Success() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Java 17"))
                .andExpect(jsonPath("$.content[1].name").value("PostgreSql 15"));
    }

    @Test
    @DisplayName("GET /api/resources?page=0&size=5 - Deve listar com paginação")
    @WithMockUser
    void testGetAllResourceTypes_WithPagination() throws Exception {

        for (int i = 0; i < 10; i++) {
            Resource resource = new Resource();
            resource.setName("Recurso " + i);
            resource.setDescription("Descrição do recurso " + i);
            resource.setResourceType(languageType);
            resource.setActive(true);
            resource.setCodeSnippet(List.of("echo 'comando " + i + "'"));
            resourceRepository.save(resource);
        }

        mockMvc.perform(get("/api/resources")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(12)) // 2 + 10
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }
}