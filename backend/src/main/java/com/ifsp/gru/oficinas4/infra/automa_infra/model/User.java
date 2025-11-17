package com.ifsp.gru.oficinas4.infra.automa_infra.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role;

}
