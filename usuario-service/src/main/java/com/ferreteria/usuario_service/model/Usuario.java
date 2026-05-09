package com.ferreteria.usuario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    //ID Generado automaticamente por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del usuario
    @Column(nullable = false)
    private String nombre;

    // El email debe ser único porque será el "nombre de usuario" para el login
    @Column(nullable = false, unique = true)
    private String email;
}