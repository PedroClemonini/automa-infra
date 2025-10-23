package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.ServiceApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationServiceRepository extends JpaRepository<ServiceApplication, Long> {
}
