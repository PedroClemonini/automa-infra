package com.ifsp.gru.oficinas4.infra.automa_infra.Configuration;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.UserJpaEntity;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@email.com").isEmpty()) {
            UserJpaEntity admin = UserJpaEntity.builder()
                    .name("admin")
                    .email("admin@email.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build();

            userRepository.save(admin);

        }
    }
}