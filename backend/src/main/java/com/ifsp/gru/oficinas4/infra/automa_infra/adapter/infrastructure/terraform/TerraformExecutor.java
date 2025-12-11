package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.infrastructure.terraform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TerraformExecutor {


    private String runCommand(String[] command, File workDir) throws IOException, InterruptedException {
        String commandString = String.join(" ", command); // Para o log
        log.debug("Executando comando: {} no diretório: {}", commandString, workDir.getAbsolutePath());

        // ProcessBuilder deve receber o comando e os argumentos separadamente
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        String result;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            result = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            log.error("Erro ao executar comando: {}. Exit code: {}. Output: {}",
                    commandString, exitCode, result);
            throw new RuntimeException(
                    String.format("Erro ao executar comando Terraform: %s\nSaída: %s",
                            commandString, result));
        }

        return result;
    }

    public void init(File workDir) throws IOException, InterruptedException {
        runCommand(new String[]{"terraform", "init"}, workDir);
    }

    public String apply(File workDir) throws IOException, InterruptedException {
        // Adicionamos o -auto-approve AQUI, tornando a intenção clara
        return(runCommand(new String[]{"terraform", "apply", "-auto-approve"}, workDir));
    }

    public String outputJson(File workDir) throws IOException, InterruptedException {
        return runCommand(new String[]{"terraform", "output", "-json"}, workDir);
    }
}