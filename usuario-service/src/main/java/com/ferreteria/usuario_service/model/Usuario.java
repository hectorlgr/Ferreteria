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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    // El email debe ser único porque será el "nombre de usuario" para el login
    @Column(nullable = false, unique = true)
    private String email;

    // Aquí guardaremos la contraseña (la rúbrica exige que esté encriptada, 
    // así que este String almacenará el hash, no la clave en texto plano)
    @Column(nullable = false)
    private String password; 

    // Aquí definiremos si es "ADMIN" o "CLIENTE"
    // con el requisito de los 2 roles diferenciados
    @Column(nullable = false)
    private String rol; 
}