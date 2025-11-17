package com.ifsp.gru.oficinas4.infra.automa_infra.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para testes (ative em produção)
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers("/public/**", "/auth/registrar", "/login").permitAll()

                        // Endpoints do H2 Console (apenas em desenvolvimento)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Swagger/OpenAPI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Actuator
                        .requestMatchers("/actuator/**").permitAll()

                        // UserController - Operações ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // UserController - Operações autenticadas (qualquer usuário)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").authenticated()

                        // Qualquer outra requisição precisa autenticação
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()) // Adiciona HTTP Basic para testes
                .formLogin(withDefaults())
                .logout(withDefaults());

        // Permite frames do mesmo domínio (necessário para H2 Console)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}