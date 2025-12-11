package com.ifsp.gru.oficinas4.infra.automa_infra.service;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("🔍 Tentando carregar usuário com email: {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuário Não encontrado: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        log.info("✅ Usuário encontrado: {}", user.getEmail());
        log.info("📝 Role do usuário: {}", user.getRole());
        log.info("🔑 Senha criptografada: {}", user.getPassword().substring(0, 20) + "...");

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        log.info("🎫 Authorities: {}", authorities);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();

        log.info("✅ UserDetails criado com sucesso para: {}", userDetails.getUsername());

        return userDetails;
    }
}