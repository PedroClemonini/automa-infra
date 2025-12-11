package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {


    Page<Resource> findByNameContainingIgnoreCase(String name, Pageable pageable);


    Page<Resource> findByResourceTypeId(Long resourceTypeId, Pageable pageable);


    Page<Resource> findByActive(Boolean active, Pageable pageable);


    long countByActive(Boolean active);
}