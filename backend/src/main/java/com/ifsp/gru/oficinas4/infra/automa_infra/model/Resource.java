package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_type_id")
    private ResourceType resourceType;

    @Column(nullable = false, length = 255)
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String version;

    @Column(name = "code_snippet", columnDefinition = "TEXT")
    private String codeSnippet;
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
