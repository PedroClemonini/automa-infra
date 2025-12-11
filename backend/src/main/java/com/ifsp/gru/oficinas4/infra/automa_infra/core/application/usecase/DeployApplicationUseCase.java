package com.ifsp.gru.oficinas4.infra.automa_infra.core.application.usecase;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.applicationResources.port.ApplicationResourceRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ProvisioningPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeployApplicationUseCase {

    private final ApplicationRepositoryPort applicationRepository;
    private final ApplicationResourceRepositoryPort applicationResourceRepository;
    private final ProvisioningPort provisioningPort;

    @Transactional
    public Application execute(Long applicationId) {
        log.info("Iniciando Use Case de Deploy para App ID: {}", applicationId);

        // 1. Busca a Aplicação
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada: " + applicationId));


        List<List<String>> resourcesList = applicationResourceRepository.findCodeSnippetsByApplicationId(applicationId);

        try {
            // 3. Chama a Porta de Provisionamento (O Adapter fará o Terraform apply e a extração do JSON/IP)
            ProvisioningPort.ProvisioningResult result = provisioningPort.provision(app, resourcesList);

            // 4. Atualiza o Domínio com o resultado
            if (result.publicIp() != null) {
                app.setIpAddress(result.publicIp());
                app.setStatus("DEPLOYED");
                log.info("IP definido: {}", result.publicIp());
            } else {
                app.setStatus("DEPLOYED_NO_IP");
                log.warn("Deploy sem IP extraído.");
            }

            // Opcional: Você pode querer salvar o 'outputLog' em algum lugar, ou apenas logar
            log.info("Output Infra: {}", result.outputLog());

        } catch (Exception e) {
            log.error("Erro no provisionamento: {}", e.getMessage());
            app.setStatus("DEPLOY_FAILED");
            applicationRepository.save(app); // Salva o estado de erro
            throw new RuntimeException("Falha no provisionamento: " + e.getMessage(), e);
        }

        // 5. Atualiza Metadados finais
        app.setLastDeployedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(app);
    }
}