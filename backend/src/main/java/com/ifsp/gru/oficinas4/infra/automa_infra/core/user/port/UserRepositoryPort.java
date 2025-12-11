package com.ifsp.gru.oficinas4.infra.automa_infra.core.user.port;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.User;

import java.util.Optional;

// O Porto (Core) não sabe como os dados são armazenados, apenas o que é necessário.
public interface UserRepositoryPort {

    User save(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}