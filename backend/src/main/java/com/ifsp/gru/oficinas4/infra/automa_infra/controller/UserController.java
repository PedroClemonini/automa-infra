package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.user.UserResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciar usuários do sistema")
public class UserController {

    @Autowired
    private UserService userService;

    // ------------------- READ ALL -------------------

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna lista paginada de todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    // ------------------- SEARCH -------------------

    @GetMapping("/search")
    @Operation(summary = "Buscar usuários por nome", description = "Busca usuários cujo nome contenha o termo fornecido (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<Page<UserResponseDTO>> searchUserByName(
            @RequestParam String name,
            Pageable pageable
    ) {
        Page<UserResponseDTO> users = userService.searchByName(name, pageable);
        return ResponseEntity.ok(users);
    }

    // ------------------- READ ONE -------------------

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- CREATE -------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou username já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------- UPDATE -------------------

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza parcialmente os dados de um usuário existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserPatchDTO dto
    ) {
        return userService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DELETE -------------------

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // ← MUDANÇA AQUI
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema (apenas ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    })
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ------------------- MÉTODOS AUXILIARES -------------------

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário pelo seu endereço de email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Verificar se usuário existe", description = "Verifica se existe um usuário com o ID fornecido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificação realizada")
    })
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        boolean exists = userService.exists(id);
        return ResponseEntity.ok(exists);
    }
}