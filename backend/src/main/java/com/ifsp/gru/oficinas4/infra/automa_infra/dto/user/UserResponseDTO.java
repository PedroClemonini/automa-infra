package com.ifsp.gru.oficinas4.infra.automa_infra.dto.user;

public record UserResponseDTO<Role>(Long id, String name, String email, String role) {
}
