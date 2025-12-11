package com.ifsp.gru.oficinas4.infra.automa_infra.terraform;

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

    private final File dir;

    // Injetando o caminho do diretório via Spring Value (configuração)
    public TerraformDirectoryManager(@Value("${terraform.workdir:/tmp/terraform2}") String workDirPath) {
        this.dir = new File(workDirPath);
        initializeDirectory();
    }

    private void initializeDirectory() {
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                // Melhorar a mensagem de erro
                throw new RuntimeException("Falha ao criar diretório Terraform em: " + dir.getAbsolutePath());
            }
        }
        log.info("TerraformDirectoryManager inicializado. Diretório: {}", dir.getAbsolutePath());
    }

    public File getDirectory() {
        return dir;
    }

    public void writeMainTfFile(String content) throws IOException {
        File mainTf = new File(this.dir, "main.tf");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mainTf))) {
            writer.write(content);
            // Uso de flush() é redundante com o try-with-resources que fecha o writer,
            // mas mantê-lo não causa mal.
            writer.flush();
        }
        log.info("Arquivo main.tf gerado com sucesso em: {}", mainTf.getAbsolutePath());
    }
}