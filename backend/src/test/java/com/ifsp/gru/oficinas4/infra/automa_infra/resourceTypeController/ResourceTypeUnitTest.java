package com.ifsp.gru.oficinas4.infra.automa_infra.resourceTypeController;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypePatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.resourceType.ResourceTypeResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.DuplicateResourceException;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.ResourceType;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceTypeRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.ResourceTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceTypeUnitTest {

    @Mock
    private ResourceTypeRepository resourceTypeRepository;

    @InjectMocks
    private ResourceTypeService resourceTypeService;

    private ResourceType resourceType;
    private ResourceTypeRequestDTO requestDTO;
    private ResourceTypePatchDTO patchDTO;
    private ResourceTypeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        Long id = 1L;

        resourceType = ResourceType.builder()
                .id(id)
                .name("Servidor")
                .description("Recurso físico de TI")
                .build();

        requestDTO = new ResourceTypeRequestDTO("Servidor", "Recurso físico de TI");
        patchDTO = new ResourceTypePatchDTO("Servidor Dell", "Atualizado");
        responseDTO = new ResourceTypeResponseDTO(id, "Servidor", "Recurso físico de TI");
    }

    // ==================== LISTAGEM ====================

    @Test
    @DisplayName("Deve listar todos os resource types com paginação")
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceType> page = new PageImpl<>(List.of(resourceType));

        when(resourceTypeRepository.findAll(pageable)).thenReturn(page);

        Page<ResourceTypeResponseDTO> result = resourceTypeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Servidor", result.getContent().get(0).name());

        verify(resourceTypeRepository).findAll(pageable);
    }

    // ==================== BUSCA POR NOME ====================

    @Test
    @DisplayName("Deve buscar resource types por nome com sucesso")
    void testSearchByName_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceType> page = new PageImpl<>(List.of(resourceType));

        when(resourceTypeRepository.findByNameContainingIgnoreCase("Servidor", pageable))
                .thenReturn(page);

        Page<ResourceTypeResponseDTO> result =
                resourceTypeService.searchByName("Servidor", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Servidor", result.getContent().get(0).name());
    }

    // ==================== BUSCA POR ID ====================

    @Test
    @DisplayName("Deve buscar resource type por ID com sucesso")
    void testFindById_Success() {
        when(resourceTypeRepository.findById(1L)).thenReturn(Optional.of(resourceType));

        Optional<ResourceTypeResponseDTO> result =
                resourceTypeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Servidor", result.get().name());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar ID inexistente")
    void testFindById_NotFound() {
        when(resourceTypeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ResourceTypeResponseDTO> result =
                resourceTypeService.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente com findByIdOrThrow")
    void testFindByIdOrThrow_NotFound() {
        when(resourceTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> resourceTypeService.findByIdOrThrow(999L));
    }

    @Test
    @DisplayName("Deve buscar ID com sucesso usando findByIdOrThrow")
    void testFindByIdOrThrow_Success() {
        when(resourceTypeRepository.findById(1L)).thenReturn(Optional.of(resourceType));

        ResourceTypeResponseDTO dto =
                resourceTypeService.findByIdOrThrow(1L);

        assertEquals("Servidor", dto.name());
    }

    // ==================== CRIAÇÃO ====================

    @Test
    @DisplayName("Deve criar resource type com sucesso")
    void testCreate_Success() {
        when(resourceTypeRepository.existsByNameIgnoreCase("Servidor")).thenReturn(false);
        when(resourceTypeRepository.save(any(ResourceType.class))).thenReturn(resourceType);

        ResourceTypeResponseDTO result = resourceTypeService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Servidor", result.name());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar resource type com nome duplicado")
    void testCreate_DuplicateName() {
        when(resourceTypeRepository.existsByNameIgnoreCase("Servidor")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> resourceTypeService.create(requestDTO));

        verify(resourceTypeRepository, never()).save(any());
    }

    // ==================== ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Deve atualizar resource type com sucesso")
    void testUpdate_Success() {
        when(resourceTypeRepository.findById(1L)).thenReturn(Optional.of(resourceType));
        when(resourceTypeRepository.save(any(ResourceType.class))).thenReturn(resourceType);

        Optional<ResourceTypeResponseDTO> result =
                resourceTypeService.update(1L, patchDTO);

        assertTrue(result.isPresent());
        verify(resourceTypeRepository).save(any(ResourceType.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao atualizar resource type inexistente")
    void testUpdate_NotFound() {
        when(resourceTypeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ResourceTypeResponseDTO> result =
                resourceTypeService.update(999L, patchDTO);

        assertTrue(result.isEmpty());
        verify(resourceTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com nome já existente")
    void testUpdate_DuplicateName() {
        ResourceTypePatchDTO dto = new ResourceTypePatchDTO("NovoNome", null);

        when(resourceTypeRepository.findById(1L)).thenReturn(Optional.of(resourceType));
        when(resourceTypeRepository.existsByNameIgnoreCase("NovoNome")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> resourceTypeService.update(1L, dto));
    }

    @Test
    @DisplayName("Deve ignorar campos em branco no patch")
    void testUpdate_IgnoreBlank() {
        ResourceTypePatchDTO blankDTO =
                new ResourceTypePatchDTO(" "," ");

        when(resourceTypeRepository.findById(1L)).thenReturn(Optional.of(resourceType));
        when(resourceTypeRepository.save(any(ResourceType.class))).thenReturn(resourceType);

        Optional<ResourceTypeResponseDTO> result =
                resourceTypeService.update(1L, blankDTO);

        assertTrue(result.isPresent());
    }

    // ==================== DELEÇÃO ====================

    @Test
    @DisplayName("Deve deletar com sucesso")
    void testDelete_Success() {
        when(resourceTypeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(resourceTypeRepository).deleteById(1L);

        boolean result = resourceTypeService.delete(1L);

        assertTrue(result);
        verify(resourceTypeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao deletar inexistente")
    void testDelete_NotFound() {
        when(resourceTypeRepository.existsById(999L)).thenReturn(false);

        boolean result = resourceTypeService.delete(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar inexistente com deleteOrThrow")
    void testDeleteOrThrow_NotFound() {
        when(resourceTypeRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> resourceTypeService.deleteOrThrow(999L));
    }

    @Test
    @DisplayName("Deve deletar com sucesso usando deleteOrThrow")
    void testDeleteOrThrow_Success() {
        when(resourceTypeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(resourceTypeRepository).deleteById(1L);

        assertDoesNotThrow(() -> resourceTypeService.deleteOrThrow(1L));
    }

    // ==================== VALIDAÇÕES ====================

    @Test
    @DisplayName("Deve verificar se existe por ID")
    void testExistsById() {
        when(resourceTypeRepository.existsById(1L)).thenReturn(true);

        assertTrue(resourceTypeService.exists(1L));
    }

    @Test
    @DisplayName("Deve verificar se nome existe")
    void testExistsByName() {
        when(resourceTypeRepository.existsByNameIgnoreCase("Servidor")).thenReturn(true);

        assertTrue(resourceTypeService.nameExists("Servidor"));
    }

}
