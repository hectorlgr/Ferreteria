package com.ferreteria.inventario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aquí guardamos el ID del producto que viene del catalogo-service.
    // Usamos unique = true porque un producto solo debe tener un registro de inventario.
    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId; 

    @Column(nullable = false)
    private Integer cantidad; // El stock actual
}