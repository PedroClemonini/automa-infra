package com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import java.util.List;

public interface ProvisioningPort {
    ProvisioningResult provision(Application application, List<List<String>> resourceSnippets);

    record ProvisioningResult(String publicIp, String outputLog) {}
}