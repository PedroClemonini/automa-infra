package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.ApplicationResource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationResourceRepository extends JpaRepository<ApplicationResource, Long> {

    boolean existsByApplicationIdAndResourcesId(Long applicationId, Long resourceId);

    @Query("SELECT ar.resources FROM ApplicationResource ar WHERE ar.application.id = :applicationId")
    List<Resource> findResourcesByApplicationId(@Param("applicationId") Long applicationId);

}