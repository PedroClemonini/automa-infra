package com.ifsp.gru.oficinas4.infra.automa_infra.core.user.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.user.port.UserRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.AuthenticationException; // Assumindo uma exceção para falha de login
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User execute(String email, String rawPassword) {
        // 1. Busca o usuário pelo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Credenciais inválidas."));

        // 2. Verifica a senha
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationException("Credenciais inválidas.");
        }

        // 3. Regra de Segurança: Remove a senha do objeto de domínio antes de devolvê-lo
        // Isso garante que a senha criptografada não vaze para camadas superiores (Controller/API)
        user.setPassword(null);

        return user;
    }
}