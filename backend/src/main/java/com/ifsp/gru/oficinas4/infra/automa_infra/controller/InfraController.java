package com.ifsp.gru.oficinas4.infra.automa_infra.controller;

import com.ifsp.gru.oficinas4.infra.automa_infra.dto.ReceivedVmInfoDto;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.ResponseDto;
import com.ifsp.gru.oficinas4.infra.automa_infra.model.Vm;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/infra")
public class InfraController {


    @PostMapping
    @Transactional
    public ResponseDto createVm(@RequestBody ReceivedVmInfoDto vmInformation) {
        var vm = new Vm(vmInformation);
        return  new ResponseDto(vm);
    }

}
