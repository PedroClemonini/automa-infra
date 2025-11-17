package com.ifsp.gru.oficinas4.infra.automa_infra.dto.user;

import jakarta.validation.constraints.Email;

public record UserRequestDTO(String name, @Email(message = "Email inválido") String email, String password, String role) {
}
