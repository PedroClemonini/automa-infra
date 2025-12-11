package com.ifsp.gru.oficinas4.infra.automa_infra.terraform;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TerraformFileBuilder {

    private final File dir;

    public TerraformFileBuilder() {
        this.dir = new File("/tmp/terraform2");
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Falha ao criar diretório Terraform");
            }
        }
        log.info("TerraformFileBuilder inicializado. Diretório: {}", dir.getAbsolutePath());
    }

    private String buildTerraformContent(Application application, List<List<String>> resourcesList) {

        StringBuilder yamlBuilder = new StringBuilder();
        yamlBuilder.append("#cloud-config\n");
        yamlBuilder.append("package_update: true\n");
        yamlBuilder.append("package_upgrade: true\n");
        yamlBuilder.append("users:\n");
        yamlBuilder.append("  - default\n");
        yamlBuilder.append("  - name: ").append(application.getSshUser()).append("\n");
        yamlBuilder.append("    shell: /bin/bash\n");
        yamlBuilder.append("    sudo: ALL=(ALL) NOPASSWD:ALL\n");
        yamlBuilder.append("    lock_passwd: false\n");
        yamlBuilder.append("chpasswd:\n");
        yamlBuilder.append("  expire: false\n");
        yamlBuilder.append("  list: |\n");
        yamlBuilder.append("    ").append(application.getSshUser()).append(":")
                .append(application.getSshPassword()).append("\n");
        yamlBuilder.append("runcmd:\n");

        if (resourcesList != null && !resourcesList.isEmpty()) {
            for (List<String> cmdList : resourcesList) {
                for (String cmd : cmdList) {
                    String cleanCmd = cmd.trim();

                    if (cleanCmd.endsWith(",")) {
                        cleanCmd = cleanCmd.substring(0, cleanCmd.length() - 1).trim();
                    }

                    if (!cleanCmd.isEmpty()) {
                        String escapedCmd = cleanCmd.replace("\"", "\\\"");
                        if (needsQuotes(cleanCmd)) {
                            yamlBuilder.append("  - \"").append(escapedCmd).append("\"\n");
                        } else {
                            yamlBuilder.append("  - ").append(cleanCmd).append("\n");
                        }
                    }
                }
            }
        } else {
            yamlBuilder.append("  - echo 'Nenhum comando adicional configurado'\n");
        }


        String generatedCloudInitYaml = yamlBuilder.toString();

        return String.format("""
    # ==== Terraform auto-generated file ====
    terraform {
      required_providers {
        proxmox = {
          source  = "bpg/proxmox"
          version = "0.77.1"
        }
      }
    }

    provider "proxmox" {
      endpoint  = "https://10.8.0.4:8006"
      insecure  = true
      api_token = "root@pam!automacao=082977ce-2211-467e-a989-79d87b3627ed"
      
      ssh {
          agent    = true
          username = "root"
          password = "@Meninas03"

          node {
            name    = "srv60"
            address = "10.8.0.7"
          }
        }
    }
    
    resource "proxmox_virtual_environment_file" "cloud_init_script" {
      content_type = "snippets"
      datastore_id = "local"
      node_name    = "srv60"

      source_raw {
        file_name = "install-%s.yaml"
        data = <<-EOF
%s
        EOF
      }
    }

    resource "proxmox_virtual_environment_vm" "ubuntu_vm2" {
        node_name = "srv60"
        name = "%s"
        description = "%s"
        tags = ["terraform", "ubuntu", "api-created"]

        clone {
            vm_id = 9001
            full  = true
        }

        agent { 
            enabled = true 
        }

        cpu { 
            cores = 4 
            type = "host" 
        }

        memory { 
            dedicated = 4096
        }

        network_device { 
            bridge = "vmbr0" 
        }
        
        network_device {
            bridge = "vxnet"
        }

        disk {
            interface    = "scsi0"
            size         = 20
            file_format  = "raw"
            datastore_id = "local-lvm"
        }

        operating_system { 
            type = "l26"
        }

        initialization {
            ip_config {
                ipv4 {
                    address = "dhcp"
                }
            }

            ip_config {
                ipv4 {
                    address = "10.10.10.90/24"
                }
            }

            dns {
                servers = ["8.8.8.8", "8.8.4.4"]
                domain  = "home"
            }


            user_data_file_id = proxmox_virtual_environment_file.cloud_init_script.id
        }
        
        depends_on = [proxmox_virtual_environment_file.cloud_init_script]
    }

    output "vm_ip_addresses" {
      description = "IP addresses of the created VM"
      value       = proxmox_virtual_environment_vm.ubuntu_vm2.ipv4_addresses
    }
    """,
                application.getName().replaceAll("\\s+", "-").toLowerCase(),
                generatedCloudInitYaml,
                application.getName(),
                application.getDescription()
        );
    }

    private boolean needsQuotes(String cmd) {

        return cmd.contains(":") ||
                cmd.contains("#") ||
                cmd.contains("'") ||
                cmd.contains("&") ||
                cmd.contains("*") ||
                cmd.contains("!") ||
                cmd.contains("|") ||
                cmd.contains(">") ||
                cmd.contains("[") ||
                cmd.contains("]") ||
                cmd.contains("{") ||
                cmd.contains("}");
    }

    public String buildAndApplyTerraformFile(Application application, List<List<String>> resources)
            throws IOException, InterruptedException {

        log.info("Iniciando deploy da aplicação: {}", application.getName());

        File mainTf = new File(this.dir, "main.tf");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mainTf))) {
            writer.write(buildTerraformContent(application, resources));
            writer.flush();
        }

        log.info("Arquivo main.tf gerado com sucesso");

        runCommand("terraform init");

        runCommand("terraform apply -auto-approve");

        String output = runCommand("terraform output -json");

        log.info("Deploy concluído. Output: {}", output);

        return output;
    }

    private String runCommand(String command) throws IOException, InterruptedException {
        log.debug("Executando comando: {}", command);

        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        pb.directory(this.dir);
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
}