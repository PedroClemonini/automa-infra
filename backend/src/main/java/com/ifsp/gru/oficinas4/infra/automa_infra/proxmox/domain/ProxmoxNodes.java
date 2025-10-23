package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.domain;
import lombok.Data;

import java.util.List;

@Data
public class ProxmoxNodes {
    private List<Node> data; // <- precisa ser "data"
    @Data
    public static class Node {
        private String node;
        private String status;
        private String id;
        private long maxmem;
        private long mem;
        private long maxdisk;
        private long disk;
        private double cpu;
        private int maxcpu;
        private String ssl_fingerprint;
        private String type;
        private String level;
        private long uptime;
    }



}
