package com.challenge.challenge.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class RoleEntity {

    // Identificador único del rol
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del rol, utilizado como un Enum para definir roles predefinidos
    @Enumerated(EnumType.STRING)
    private RoleName name;

}