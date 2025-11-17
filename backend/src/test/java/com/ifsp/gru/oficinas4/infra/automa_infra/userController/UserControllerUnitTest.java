package com.ifsp.gru.oficinas4.infra.automa_infra.userController;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.DuplicateResourceException;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.UserRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;
    private UserPatchDTO userPatchDTO;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        user = new User();
        user.setName("João");
        user.setEmail("joao@email.com");
        user.setPassword("teste123");
        user.setRole("COMMON");

        userRequestDTO = new UserRequestDTO("João", "joao@email.com", "joao123", "COMMON");
        userResponseDTO = new UserResponseDTO(id, "João", "joao@email.com", "COMMON");
        userPatchDTO = new UserPatchDTO("João Santos", null, null, "ADMIN");
    }

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @DisplayName("Deve listar todos os usuários com paginação")
    void testFindAll_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserResponseDTO> result = userService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("João", result.getContent().get(0).name());
        assertEquals("joao@email.com", result.getContent().get(0).email());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar usuários por nome com sucesso")
    void testSearchByName_Success() {
        // Arrange
        String name = "João";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findByNameContainingIgnoreCase(name, pageable)).thenReturn(userPage);

        // Act
        Page<UserResponseDTO> result = userService.searchByName(name, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("João", result.getContent().get(0).name());

        verify(userRepository).findByNameContainingIgnoreCase(name, pageable);
    }

    // ==================== TESTES DE BUSCA POR ID ====================

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void testFindById_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<UserResponseDTO> result = userService.findById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("João", result.get().name());
        assertEquals("joao@email.com", result.get().email());

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não existe")
    void testFindById_NotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDTO> result = userService.findById(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando buscar por ID inexistente com findByIdOrThrow")
    void testFindByIdOrThrow_ThrowsException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findByIdOrThrow(userId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso usando findByIdOrThrow")
    void testFindByIdOrThrow_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findByIdOrThrow(userId);

        // Assert
        assertNotNull(result);
        assertEquals("João", result.name());
        assertEquals("joao@email.com", result.email());
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCreate_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaHasheada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDTO result = userService.create(userRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("João", result.name());
        assertEquals("joao@email.com", result.email());

        verify(userRepository).existsByEmail(userRequestDTO.email());
        verify(userRepository).existsByName(userRequestDTO.name());
        verify(passwordEncoder).encode("joao123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email duplicado")
    void testCreate_DuplicateEmail() {
        // Arrange
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.create(userRequestDTO)
        );

        assertTrue(exception.getMessage().contains("joao@email.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com username duplicado")
    void testCreate_DuplicateUsername() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(userRequestDTO.name())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.create(userRequestDTO)
        );

        assertTrue(exception.getMessage().contains("João"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve criar usuário mesmo com name null")
    void testCreate_WithNullName() {
        // Arrange
        UserRequestDTO dtoWithNullName = new UserRequestDTO(null, "novo@email.com", "senha123", "COMMON");
        User userWithNullName = new User();
        userWithNullName.setName(null);
        userWithNullName.setEmail("novo@email.com");
        userWithNullName.setPassword("senhaHasheada");
        userWithNullName.setRole("COMMON");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaHasheada");
        when(userRepository.save(any(User.class))).thenReturn(userWithNullName);

        // Act
        UserResponseDTO result = userService.create(dtoWithNullName);

        // Assert
        assertNotNull(result);
        assertNull(result.name());
        assertEquals("novo@email.com", result.email());

        verify(userRepository, never()).existsByName(any());
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testUpdate_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<UserResponseDTO> result = userService.update(userId, userPatchDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("João Santos", result.get().name());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao atualizar usuário inexistente")
    void testUpdate_NotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDTO> result = userService.update(userId, userPatchDTO);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar email para um já existente")
    void testUpdate_DuplicateEmail() {
        // Arrange
        Long userId = 1L;
        UserPatchDTO dtoWithNewEmail = new UserPatchDTO(null, "outro@email.com", null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@email.com")).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.update(userId, dtoWithNewEmail)
        );

        assertTrue(exception.getMessage().contains("outro@email.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar email para o mesmo email do usuário")
    void testUpdate_SameEmail() {
        // Arrange
        Long userId = 1L;
        UserPatchDTO dtoWithSameEmail = new UserPatchDTO(null, "joao@email.com", null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<UserResponseDTO> result = userService.update(userId, dtoWithSameEmail);

        // Assert
        assertTrue(result.isPresent());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas senha quando apenas senha for fornecida")
    void testUpdate_OnlyPassword() {
        // Arrange
        Long userId = 1L;
        UserPatchDTO dtoWithPassword = new UserPatchDTO(null, null, "novaSenha123", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaHasheada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<UserResponseDTO> result = userService.update(userId, dtoWithPassword);

        // Assert
        assertTrue(result.isPresent());
        verify(passwordEncoder).encode("novaSenha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar role do usuário")
    void testUpdate_Role() {
        // Arrange
        Long userId = 1L;
        UserPatchDTO dtoWithRole = new UserPatchDTO(null, null, null, "ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<UserResponseDTO> result = userService.update(userId, dtoWithRole);

        // Assert
        assertTrue(result.isPresent());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção com updateOrThrow quando usuário não existe")
    void testUpdateOrThrow_ThrowsException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateOrThrow(userId, userPatchDTO)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve ignorar campos vazios na atualização")
    void testUpdate_IgnoreBlankFields() {
        // Arrange
        Long userId = 1L;
        UserPatchDTO dtoWithBlankFields = new UserPatchDTO("", "   ", "", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<UserResponseDTO> result = userService.update(userId, dtoWithBlankFields);

        // Assert
        assertTrue(result.isPresent());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDelete_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act
        boolean result = userService.delete(userId);

        // Assert
        assertTrue(result);
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Deve retornar false ao deletar usuário inexistente")
    void testDelete_NotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        boolean result = userService.delete(userId);

        // Assert
        assertFalse(result);
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente com deleteOrThrow")
    void testDeleteOrThrow_ThrowsException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteOrThrow(userId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    @DisplayName("Deve deletar com sucesso usando deleteOrThrow")
    void testDeleteOrThrow_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act & Assert
        assertDoesNotThrow(() -> userService.deleteOrThrow(userId));

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Deve verificar se usuário existe")
    void testExists_ReturnsTrue() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        boolean result = userService.exists(userId);

        // Assert
        assertTrue(result);
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não existe")
    void testExists_ReturnsFalse() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        boolean result = userService.exists(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void testEmailExists_ReturnsTrue() {
        // Arrange
        String email = "joao@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = userService.emailExists(email);

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void testEmailExists_ReturnsFalse() {
        // Arrange
        String email = "naoexiste@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act
        boolean result = userService.emailExists(email);

        // Assert
        assertFalse(result);
    }

    // ==================== TESTES DE BUSCA POR EMAIL ====================

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void testFindByEmail_Success() {
        // Arrange
        String email = "joao@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<UserResponseDTO> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().email());
        assertEquals("João", result.get().name());

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando email não existe")
    void testFindByEmail_NotFound() {
        // Arrange
        String email = "naoexiste@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDTO> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(email);
    }
}