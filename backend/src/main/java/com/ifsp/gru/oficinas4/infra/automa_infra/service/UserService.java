package com.ifsp.gru.oficinas4.infra.automa_infra.service;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.DuplicateResourceException;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Converte DTO de requisição para entidade User
     * Método manual para criar nova instância
     */
    private User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchByName(String name, Pageable pageable) {
        Page<User> users = userRepository.findByNameContainingIgnoreCase(name, pageable);
        return users.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByIdOrThrow(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        // Validação: verifica se email já existe
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Email já cadastrado: " + dto.email());
        }

        // Validação: verifica se username já existe
        if (dto.name() != null && userRepository.existsByName(dto.name())) {
            throw new DuplicateResourceException("Username já cadastrado: " + dto.name());
        }

        // Converte DTO para Entidade
        User user = toEntity(dto);

        // Criptografa a senha
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Salva no banco
        User savedUser = userRepository.save(user);

        // Retorna DTO de resposta
        return toResponseDTO(savedUser);
    }

    /**
     * Atualiza parcialmente um usuário existente
     */
    @Transactional
    public Optional<UserResponseDTO> update(Long id, UserPatchDTO dto) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User existingUser = optionalUser.get();

        // Atualiza apenas os campos fornecidos (não nulos)
        if (dto.name() != null && !dto.name().isBlank()) {
            existingUser.setName(dto.name());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            // Verifica se o novo email já existe em outro usuário
            if (!existingUser.getEmail().equals(dto.email()) &&
                    userRepository.existsByEmail(dto.email())) {
                throw new DuplicateResourceException("Email já cadastrado: " + dto.email());
            }
            existingUser.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(dto.password());
            existingUser.setPassword(hashedPassword);
        }

        if (dto.role() != null) {
            existingUser.setRole(dto.role());
        }

        // Salva as alterações
        User updatedUser = userRepository.save(existingUser);

        return Optional.of(toResponseDTO(updatedUser));
    }

    @Transactional
    public UserResponseDTO updateOrThrow(Long id, UserPatchDTO dto) {
        return update(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    @Transactional
    public void deleteOrThrow(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toResponseDTO);
    }
}