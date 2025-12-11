package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.infrastructure.terraform;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ProvisioningPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.core.domain.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TerraformDeploymentService implements ProvisioningPort {

    private final TerraformContentGenerator contentGenerator;
    private final TerraformDirectoryManager directoryManager;
    private final TerraformExecutor executor;

    @Override
    public ProvisioningResult provision(Application application, List<List<String>> resourceSnippets) {

        if (application.getId() == null) {
            log.error("Tentativa de provisionamento com Application ID nulo.");
            throw new BusinessException("A Aplicação deve ser salva e possuir um ID antes do deploy.");
        }

        log.info("Iniciando provisionamento Terraform para a aplicação ID: {}", application.getId());

        try {

            File workDir = directoryManager.getOrCreateWorkDir(application.getId());


            String cloudConfigYaml = contentGenerator.generateCloudConfigYaml(application, resourceSnippets);
            String mainTfContent = contentGenerator.generateMainTfContent(application, cloudConfigYaml);

            directoryManager.writeMainTfFile(workDir, mainTfContent);

            executor.init(workDir);
            String applyLog = executor.apply(workDir);

            String outputJson = executor.outputJson(workDir);

            String publicIp = extractPublicIpFromOutput(outputJson);

            log.info("Provisionamento concluído. IP: {}", publicIp);

            // 6. RETORNO DO RESULTADO
            return new ProvisioningResult(publicIp, applyLog);

        } catch (Exception e) {
            log.error("Erro fatal durante o provisionamento Terraform para {}: {}", application.getName(), e.getMessage(), e);
            throw new ProvisioningFailureException("Falha ao executar provisionamento Terraform. Detalhes: " + e.getMessage(), e);
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private String extractPublicIpFromOutput(String outputJson) {
        // Esta função deve conter a lógica de parse do JSON de saída do Terraform
        // para obter o endereço IP provisionado.
        // Por ser complexo e depender do formato, mantemos como placeholder:
        return "IP_PROVISIONADO_VIA_TERRAFORM";
    }

    // Exceção de Infraestrutura
    public static class ProvisioningFailureException extends RuntimeException {
        public ProvisioningFailureException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}