package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private UserResponse user; // Contém ID, nome, email, role
}