package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user;

 // Importar novo DTO
import com.ifsp.gru.oficinas4.infra.automa_infra.Configuration.JwtService;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserLoginRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.dto.UserRegisterRequest;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.user.mapper.UserWebMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.user.usecases.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // Importar
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Importar
import org.springframework.security.core.userdetails.UserDetails; // Importar
import org.springframework.security.core.userdetails.UserDetailsService; // Importar
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUseCase;
    // Removemos o AuthenticateUserUseCase, pois a autenticação será feita pelo Spring Security
    // private final AuthenticateUserUseCase authenticateUseCase;

    private final UserWebMapper mapper;

    // Novas injeções para JWT
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // Para buscar o usuário após a autenticação

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRegisterRequest request) {

        User newUser = mapper.toDomain(request);

        // O Use Case salva e criptografa a senha
        User registeredUser = registerUseCase.execute(newUser);

        // Geração do token após o registro (opcional, mas comum)
        // O UserDomain deve ser tratado como UserDetails aqui.
        final UserDetails userDetails = (UserDetails) registeredUser;
        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.builder()
                        .token(jwt)
                        .user(mapper.toResponse(registeredUser))
                        .build()
        );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserLoginRequest request) {

        // 1. AUTENTICAÇÃO: Verifica credenciais usando o AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. BUSCA: Se a autenticação for bem-sucedida, busca os detalhes do usuário
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // O Core Use Case ainda é útil para limpar a senha do objeto de domínio antes do mapping
        // Assumindo que o seu AuthenticateUserUseCase foi simplificado ou será removido
        User userDomain = (User) userDetails;

        // 3. GERAÇÃO DO TOKEN
        final String jwt = jwtService.generateToken(userDetails);

        // 4. RESPOSTA: Retorna o token e os dados do usuário
        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(jwt)
                        .user(mapper.toResponse(userDomain))
                        .build()
        );
    }
}