package com.ifsp.gru.oficinas4.infra.automa_infra.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_resources", uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "resource_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ApplicationResourceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id")
    private ApplicationJpaEntity application;


    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceJpaEntity resources;

    @Column(name = "added_at")
    private LocalDateTime addedAt = LocalDateTime.now();

}
