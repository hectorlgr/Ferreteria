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

    // Apellido del usuario
    @Column(nullable = false)
    private String apellido;

    // El email debe ser único porque será el "nombre de usuario" para el login
    @Column(nullable = false, unique = true)
    private String email;

    // Aquí guardaremos la contraseña
    // Este String almacenará el hash, no la clave en texto plano
    @Column(nullable = false)
    private String password; 

    // Aquí definiremos si es "ADMIN" o "CLIENTE"
    @Column(nullable = false)
    private String rol; 
}