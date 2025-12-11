package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "application_resources", uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "resource_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ApplicationResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id")
    private Application application;


    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resources;

    @Column(name = "added_at")
    private LocalDateTime addedAt = LocalDateTime.now();

}
