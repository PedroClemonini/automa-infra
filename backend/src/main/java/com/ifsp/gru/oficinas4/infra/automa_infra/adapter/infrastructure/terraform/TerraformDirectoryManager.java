package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.infrastructure.terraform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class TerraformDirectoryManager {

    private final File baseDir;

    public TerraformDirectoryManager(@Value("${terraform.workdir:/tmp/terraform_automa}") String workDirPath) {

        this.baseDir = new File(workDirPath);
        initializeBaseDirectory();
    }

    private void initializeBaseDirectory() {
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (!created) {

                throw new RuntimeException("Falha ao criar diretório base do Terraform em: " + baseDir.getAbsolutePath());
            }
        }
        log.info("TerraformDirectoryManager inicializado. Diretório base: {}", baseDir.getAbsolutePath());
    }


    public File getOrCreateWorkDir(Long applicationId) {
        File appDir = new File(baseDir, String.valueOf(applicationId));
        if (!appDir.exists()) {
            boolean created = appDir.mkdirs();
            if (!created) {
                String errorMsg = "Falha ao criar diretório da aplicação em: " + appDir.getAbsolutePath();
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
        log.debug("Diretório de trabalho da aplicação ID {} é: {}", applicationId, appDir.getAbsolutePath());
        return appDir;
    }


    public void writeMainTfFile(File workDir, String content) throws IOException {

        if (!workDir.isDirectory()) {
            throw new IOException("O caminho fornecido não é um diretório válido: " + workDir.getAbsolutePath());
        }

        File mainTf = new File(workDir, "main.tf");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mainTf))) {
            writer.write(content);

            writer.flush();
        }
        log.info("Arquivo main.tf gerado com sucesso em: {}", mainTf.getAbsolutePath());
    }

}