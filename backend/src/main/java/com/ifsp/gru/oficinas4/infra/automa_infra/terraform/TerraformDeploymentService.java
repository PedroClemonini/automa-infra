package com.ifsp.gru.oficinas4.infra.automa_infra.terraform;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service // Usa @Service para indicar que é uma camada de serviço/negócio
@RequiredArgsConstructor // Cria um construtor com os campos final injetados (Spring)
public class TerraformDeploymentService {

    private final TerraformContentGenerator contentGenerator;
    private final TerraformDirectoryManager directoryManager;
    private final TerraformExecutor executor;

    public String deployApplication(Application application, List<List<String>> resources)
            throws IOException, InterruptedException {

        log.info("Iniciando deploy da aplicação: {}", application.getName());

        String cloudConfigYaml = contentGenerator.generateCloudConfigYaml(application, resources);
        String mainTfContent = contentGenerator.generateMainTfContent(application, cloudConfigYaml);

        directoryManager.writeMainTfFile(mainTfContent);

        File workDir = directoryManager.getDirectory();

        executor.init(workDir);

        executor.apply(workDir);

        String output = executor.outputJson(workDir);

        log.info("Deploy concluído. Output: {}", output);

        return output;
    }
}