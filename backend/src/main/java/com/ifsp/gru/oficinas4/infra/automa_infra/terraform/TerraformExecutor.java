package com.ifsp.gru.oficinas4.infra.automa_infra.terraform;

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


    public String runCommand(String command, File workDir) throws IOException, InterruptedException {
        log.debug("Executando comando: {} no diretório: {}", command, workDir.getAbsolutePath());


        String finalCommand = command.equals("terraform apply") ? command + " -auto-approve" : command;

        ProcessBuilder pb = new ProcessBuilder(finalCommand.split(" "));
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
                    command, exitCode, result);
            throw new RuntimeException(
                    String.format("Erro ao executar comando Terraform: %s\nSaída: %s",
                            command, result));
        }

        return result;
    }

    public void init(File workDir) throws IOException, InterruptedException {
        runCommand("terraform init", workDir);
    }

    public void apply(File workDir) throws IOException, InterruptedException {
        // O -auto-approve é adicionado no runCommand para evitar duplicação.
        runCommand("terraform apply", workDir);
    }

    public String outputJson(File workDir) throws IOException, InterruptedException {
        return runCommand("terraform output -json", workDir);
    }
}