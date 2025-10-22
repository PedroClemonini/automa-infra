package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.Vm;

public record ResponseDto(String message, String ip) {
    public ResponseDto(Vm vm) {
        this("Vm feita com sucesso ", vm.getIp());
    }
}
