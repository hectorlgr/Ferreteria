package com.ferreteria.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // ID Generado automaticamente por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email único para el login
    @Column(nullable = false, unique = true)
    private String email;

    // Contraseña
    @Column(nullable = false)
    private String password;

    // Rol del usuario ("ADMIN", "CLIENTE", "OPERADOR")
    @Column(nullable = false)
    private String role;
}