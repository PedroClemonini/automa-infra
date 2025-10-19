package com.ifsp.gru.oficinas4.infra.automa_infra.model;
import com.ifsp.gru.oficinas4.infra.automa_infra.dto.ReceivedVmInfoDto;
import jakarta.persistence.*;

@Entity
@Table(name = "vms")
public class Vm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int core;
    private int ram;
    @Enumerated(EnumType.STRING)
    private OperationalSystem operationalSystem;
    private String ip;
    private int storage;

    public Vm(){}


    public Vm(ReceivedVmInfoDto vmInformation) {
        this.core = vmInformation.core();
        this.ram = vmInformation.ram();
        this.operationalSystem = vmInformation.operationalSystem();
        this.ip = vmInformation.ip();
        this.storage = vmInformation.storage();
    }

    public String getIp() {
        return ip;
    }
}
