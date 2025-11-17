package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

}
