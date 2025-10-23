package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ServiceApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String server;
    private int port;

    public ServiceApplication(){}
}

