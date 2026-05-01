package com.ferreteria.catalogo_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // Ej: "Taladro Percutor", "Martillo Carpintero"

    private String descripcion;

    private String marca; // Ej: "Makita", "Bosch", "Stanley"

    @Column(nullable = false)
    private Integer precio; // Usamos Integer, ideal para trabajar con pesos chilenos sin decimales
}