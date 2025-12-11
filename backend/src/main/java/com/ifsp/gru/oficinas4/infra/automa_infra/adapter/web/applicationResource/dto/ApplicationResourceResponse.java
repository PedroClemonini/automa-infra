package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.applicationResource.dto;


import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.application.dto.ApplicationResponse;
import com.ifsp.gru.oficinas4.infra.automa_infra.adapter.web.resource.dto.ResourceResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResourceResponse {
    private Long id;
    private LocalDateTime addedAt;

    // Entidades aninhadas
    private ApplicationResponse application;
    private ResourceResponse resources; // Nome do campo no domínio é 'resources'
}