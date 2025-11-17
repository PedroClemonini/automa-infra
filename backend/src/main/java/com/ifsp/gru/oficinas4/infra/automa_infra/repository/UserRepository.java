package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByName(String name);
}
