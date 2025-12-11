package com.ifsp.gru.oficinas4.infra.automa_infra.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


public class ApplicationResource {

    private Long id;

    private Application application;

    private Resource resources;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Resource getResources() {
        return resources;
    }

    public void setResources(Resource resources) {
        this.resources = resources;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    private LocalDateTime addedAt = LocalDateTime.now();

}
