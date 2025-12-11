package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository  extends JpaRepository<Application, Long> {

    Page<Application> findByNameContainingIgnoreCase(String name, Pageable pageable);


    Page<Application> findByStatus(String status, Pageable pageable);

    List<Application> findByStatus(String status);


    Page<Application> findByCreatedById(Long userId, Pageable pageable);

    List<Application> findByCreatedById(Long userId);


    Page<Application> findByCreatedByIdAndStatus(Long userId, String status, Pageable pageable);


    Page<Application> findByNameContainingIgnoreCaseAndStatus(String name, String status, Pageable pageable);

    Page<Application> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);


    Page<Application> findByLastDeployedAtAfter(LocalDateTime date, Pageable pageable);


    Page<Application> findByLastDeployedAtIsNull(Pageable pageable);


    long countByStatus(String status);


    long countByCreatedById(Long userId);

    long countByCreatedByIdAndStatus(Long userId, String status);

    @Query("SELECT a FROM Application a WHERE a.createdBy.id = :userId ORDER BY a.createdAt DESC")
    Page<Application> findRecentApplicationsByUser(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT a FROM Application a WHERE a.lastDeployedAt >= :date ORDER BY a.lastDeployedAt DESC")
    Page<Application> findRecentlyDeployed(@Param("date") LocalDateTime date, Pageable pageable);


    @Query("SELECT a FROM Application a WHERE " +
            "(:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:userId IS NULL OR a.createdBy.id = :userId)")
    Page<Application> searchApplications(
            @Param("name") String name,
            @Param("status") String status,
            @Param("userId") Long userId,
            Pageable pageable
    );


    boolean existsByNameAndCreatedById(String name, Long userId);

    @Query("SELECT a FROM Application a WHERE a.createdAt < :createdBefore AND a.lastDeployedAt IS NULL")
    Page<Application> findApplicationsNeedingAttention(
            @Param("createdBefore") LocalDateTime createdBefore,
            Pageable pageable
    );
}
