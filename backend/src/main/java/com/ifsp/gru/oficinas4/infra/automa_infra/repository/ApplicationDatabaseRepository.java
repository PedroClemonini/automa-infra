package com.ifsp.gru.oficinas4.infra.automa_infra.repository;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.DatabaseApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationDatabaseRepository extends JpaRepository<DatabaseApplication, Long> {
}
