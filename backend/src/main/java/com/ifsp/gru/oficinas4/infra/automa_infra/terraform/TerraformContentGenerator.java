package com.ifsp.gru.oficinas4.infra.automa_infra.terraform;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.Configuration.ProxmoxApiConfig; // Importa a nova classe de configuração
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TerraformContentGenerator {

    private final ProxmoxApiConfig proxmoxConfig;


    public String generateCloudConfigYaml(Application application, List<List<String>> resourcesList) {
        StringBuilder yamlBuilder = new StringBuilder();
        yamlBuilder.append("#cloud-config\n");
        yamlBuilder.append("bootcmd:\n");
        yamlBuilder.append("    - [ sh, -c, \"echo 'Verificando conectividade...'; timeout 60s sh -c 'until ping -c 1 -W 1 8.8.8.8; do sleep 2; done'\" ]\n");
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

        return yamlBuilder.toString();
    }

    /**
     * Gera o conteúdo completo do arquivo main.tf, utilizando a configuração injetada.
     */
    public String generateMainTfContent(Application application, String cloudInitYaml) {
        String safeFileName = application.getName().replaceAll("\\s+", "-").toLowerCase();

        // Valores obtidos da classe de configuração
        String endpoint = proxmoxConfig.getEndpoint();
        String apiToken = proxmoxConfig.getApiToken();
        boolean insecure = proxmoxConfig.isInsecure();
        String sshUser = proxmoxConfig.getSshUsername();
        String sshPass = proxmoxConfig.getSshPassword();
        String nodeName = proxmoxConfig.getNodeName();
        String nodeAddress = proxmoxConfig.getNodeAddress();

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
              endpoint  = "%s"
              insecure  = %s
              api_token = "%s"
              
              ssh {
                  agent    = true
                  username = "%s"
                  password = "%s"
            
                  node {
                    name    = "%s"
                    address = "%s"
                  }
                }
            }
            
            resource "proxmox_virtual_environment_file" "cloud_init_script" {
              content_type = "snippets"
              datastore_id = "local"
              node_name    = "%s"
            
              source_raw {
                file_name = "install-%s.yaml"
                data = <<-EOF
%s
                EOF
              }
            }
            
            resource "proxmox_virtual_environment_vm" "ubuntu_vm2" {
                node_name = "%s"
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
                endpoint,
                insecure,
                apiToken,
                sshUser,
                sshPass,
                nodeName,
                nodeAddress,
                nodeName, // node_name para cloud_init_script
                safeFileName,
                cloudInitYaml,
                nodeName, // node_name para proxmox_virtual_environment_vm
                application.getName(),
                application.getDescription()
        );
    }

    /**
     * Lógica para determinar se um comando precisa ser envolvido por aspas no YAML.
     */
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
}