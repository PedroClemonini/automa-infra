package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.ReceivedVmInfoDto;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.ResponseDto;
import com.ifsp.gru.oficinas4.infra.automa_infra.service.VirtualMachineService; // Importar o Service
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/infra/vms") // Mudei o endpoint para ser mais específico
public class VirtualMachineController {

    private final VirtualMachineService vmService; // 1. Declarar o Service

    public VirtualMachineController(VirtualMachineService vmService) {
        this.vmService = vmService;
    }

    @PostMapping("/clone")
    public ResponseEntity<ResponseDto> createVm(@RequestBody ReceivedVmInfoDto vmInformation) {

        // 3. Delegar a lógica ao Service e obter o ID da Task
        String taskId = vmService.cloneAndConfigureVm(vmInformation);

        // 4. Construir e retornar a resposta
        ResponseDto response = new ResponseDto("Clonagem iniciada.", taskId);

        return ResponseEntity.accepted().body(response); // Retorna HTTP 202 Accepted
    }
}