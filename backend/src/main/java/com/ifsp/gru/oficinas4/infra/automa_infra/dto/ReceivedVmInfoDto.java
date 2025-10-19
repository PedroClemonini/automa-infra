package com.ifsp.gru.oficinas4.infra.automa_infra.dto;

import com.ifsp.gru.oficinas4.infra.automa_infra.model.OperationalSystem;

public record ReceivedVmInfoDto(
        int core,
        int ram,
        OperationalSystem operationalSystem,
        String ip,
        int storage
) {
}
