package com.ifsp.gru.oficinas4.infra.automa_infra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationPatchDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationRequestDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.application.ApplicationResponseDTO;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Application;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Resource;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.User;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ApplicationResourceRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.ResourceRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.repository.UserRepository;
import com.ifsp.gru.oficinas4.infra.automa_infra.terraform.TerraformDeploymentService;
import com.ifsp.gru.oficinas4.infra.automa_infra.terraform.TerraformFileBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    @Autowired
    private ApplicationRepository repository;

    @Autowired
    private ApplicationResourceRepository applicationResourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private final TerraformDeploymentService terraformDeploymentService;
    private final ObjectMapper objectMapper;

    private ApplicationResponseDTO toResponseDTO(Application app) {
        return new ApplicationResponseDTO(
                app.getId(),
                app.getCreatedBy().getId(),
                app.getCreatedBy().getName(),
                app.getName(),
                app.getDescription(),
                app.getStatus(),
                app.getSshUser(),
                app.getIpAddress(),
                app.getCreatedAt(),
                app.getUpdatedAt(),
                app.getLastDeployedAt()
        );
    }

    private Application toEntity(ApplicationRequestDTO dto, User user) {
        Application app = new Application();
        app.setCreatedBy(user);
        app.setName(dto.name());
        app.setDescription(dto.description());
        app.setStatus(dto.status());
        app.setSshUser(dto.sshUser());
        app.setSshPassword(dto.sshPassword());
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());
        return app;
    }



    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> searchByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> findByStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> findByUserId(Long userId, Pageable pageable) {
        return repository.findByCreatedById(userId, pageable)
                .map(this::toResponseDTO);
    }


    @Transactional(readOnly = true)
    public Optional<ApplicationResponseDTO> findById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO findByIdOrThrow(Long id) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada com ID: " + id));
        return toResponseDTO(app);
    }



    @Transactional
    public ApplicationResponseDTO create(ApplicationRequestDTO dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + dto.userId()));

        Application app = toEntity(dto, user);

        Application saved = repository.save(app);

        return toResponseDTO(saved);
    }


    @Transactional
    public Optional<ApplicationResponseDTO> update(Long id, ApplicationPatchDTO dto) {
        Optional<Application> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        Application existing = optional.get();


        if (dto.name() != null && !dto.name().isBlank()) {
            existing.setName(dto.name());
        }


        if (dto.description() != null) {
            existing.setDescription(dto.description());
        }


        if (dto.status() != null && !dto.status().isBlank()) {
            existing.setStatus(dto.status());
        }


        if (dto.sshUser() != null && !dto.sshUser().isBlank()) {
            existing.setSshUser(dto.sshUser());
        }


        if (dto.sshPassword() != null && !dto.sshPassword().isBlank()) {
            existing.setSshPassword(dto.sshPassword());
        }


        existing.setUpdatedAt(LocalDateTime.now());

        Application updated = repository.save(existing);

        return Optional.of(toResponseDTO(updated));
    }

    @Transactional
    public ApplicationResponseDTO updateOrThrow(Long id, ApplicationPatchDTO dto) {
        return update(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada com ID: " + id));
    }


    @Transactional
    public Optional<ApplicationResponseDTO> updateStatus(Long id, String status) {
        Optional<Application> optional = repository.findById(id);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        Application existing = optional.get();
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());

        Application updated = repository.save(existing);

        return Optional.of(toResponseDTO(updated));
    }

    @Transactional
    public ApplicationResponseDTO updateStatusOrThrow(Long id, String status) {
        return updateStatus(id, status)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada com ID: " + id));
    }

    @Transactional
    public Optional<ApplicationResponseDTO> deploy(Long id) {
        log.info("Iniciando deploy da aplicação ID: {}", id);

        Optional<Application> optional = repository.findById(id);

        if (optional.isEmpty()) {
            log.warn("Aplicação não encontrada: {}", id);
            return Optional.empty();
        }

        Application existing = optional.get();

        List<List<String>> resourcesList = applicationResourceRepository
                .findResourcesByApplicationId(id)
                .stream()
                .map(Resource::getCodeSnippet)
                .collect(Collectors.toList());

        try {
            String terraformOutput = terraformDeploymentService
                    .deployApplication(existing, resourcesList);

            String extractedIp = extractVmIpAddress(terraformOutput);

            if (extractedIp != null) {
                existing.setIpAddress(extractedIp); // Salva o IP na Entity
                existing.setStatus("DEPLOYED");
                log.info("IP da VM extraído e definido: {}", extractedIp);
            } else {
                existing.setStatus("DEPLOYED_NO_IP");
                log.warn("Deploy concluído, mas nenhum IP válido foi extraído para a aplicação {}", id);
            }


            log.info("Terraform executado com sucesso para aplicação {}. Output: {}", id, terraformOutput);

            existing.setStatus("DEPLOYED");

        } catch (Exception e) {
            log.error("Erro ao executar deploy da aplicação {}: {}", id, e.getMessage(), e);
            existing.setStatus("DEPLOY_FAILED");
            repository.save(existing);
            throw new RuntimeException("Falha na automação Proxmox/Terraform: " + e.getMessage(), e);
        }
        existing.setStatus("DEPLOYED");
        existing.setLastDeployedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        Application updated = repository.save(existing);

        log.info("Aplicação {} deployed com sucesso", id);

        return Optional.of(toResponseDTO(updated));
    }



    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }


    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return repository.existsById(id);
    }


    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return repository.countByCreatedById(userId);
    }

    private String extractVmIpAddress(String terraformOutput) {
        try {

            var rootNode = objectMapper.readTree(terraformOutput);

            var ipAddressesNode = rootNode
                    .path("vm_ip_addresses")
                    .path("value");

            if (ipAddressesNode.isArray()) {
                for (var outerArray : ipAddressesNode) {
                    if (outerArray.isArray()) {
                        for (var innerElement : outerArray) {
                            String ip = innerElement.asText();

                            if (ip != null && !ip.startsWith("127.")) {
                                return ip;
                            }
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Falha ao analisar o output JSON do Terraform: {}", e.getMessage(), e);
            return null;
        }
    }
}