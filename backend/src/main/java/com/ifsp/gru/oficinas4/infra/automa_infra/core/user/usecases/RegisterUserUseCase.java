package com.ifsp.gru.oficinas4.infra.automa_infra.core.user.usecases;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.user.port.UserRepositoryPort;

import com.ifsp.gru.oficinas4.infra.automa_infra.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Dependência para criptografia
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder; // Injeção do componente de segurança

    @Transactional
    public User execute(User newUser) {
        // 1. Regra de Negócio: Verificar unicidade do e-mail
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new BusinessException("O e-mail " + newUser.getEmail() + " já está em uso.");
        }

        // 2. Regra de Segurança: Criptografar a senha
        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);

        // 3. Regra de Negócio: Definir o papel (Role) padrão, se não definido
        if (newUser.getRole() == null || newUser.getRole().isBlank()) {
            newUser.setRole("USER"); // Define um papel padrão
        }

        // 4. Persistência
        return userRepository.save(newUser);
    }
}