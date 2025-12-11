package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;

    // NUNCA DEVOLVE A SENHA
}