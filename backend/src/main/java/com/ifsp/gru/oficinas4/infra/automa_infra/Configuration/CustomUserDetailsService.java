package com.ifsp.gru.oficinas4.infra.automa_infra.Configuration;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.user.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Assume que seu Application Core Domain 'User' implementa UserDetails,
        // ou você tem um mapper para convertê-lo.

        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }
}