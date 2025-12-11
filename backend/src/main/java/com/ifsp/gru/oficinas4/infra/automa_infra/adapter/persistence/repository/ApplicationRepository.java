package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity.ApplicationJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {

    // Listas simples
    List<ApplicationJpaEntity> findByStatus(String status);
    List<ApplicationJpaEntity> findByCreatedById(Long userId);
    boolean existsByNameAndCreatedById(String name, Long userId);

    // Consultas Paginadas
    Page<ApplicationJpaEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ApplicationJpaEntity> findByStatus(String status, Pageable pageable);
    Page<ApplicationJpaEntity> findByCreatedById(Long userId, Pageable pageable);
    Page<ApplicationJpaEntity> findByCreatedByIdAndStatus(Long userId, String status, Pageable pageable);
    Page<ApplicationJpaEntity> findByNameContainingIgnoreCaseAndStatus(String name, String status, Pageable pageable);

    // Consultas de Data
    Page<ApplicationJpaEntity> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);
    Page<ApplicationJpaEntity> findByLastDeployedAtAfter(LocalDateTime date, Pageable pageable);
    Page<ApplicationJpaEntity> findByLastDeployedAtIsNull(Pageable pageable);

    // Consultas customizadas (seus métodos específicos)
    Page<ApplicationJpaEntity> findByCreatedByIdOrderByCreatedAtDesc(Long userId, Pageable pageable); // Para recentApplicationsByUser

    long countByStatus(String status);
    long countByCreatedById(Long userId);
    long countByCreatedByIdAndStatus(Long userId, String status);
}