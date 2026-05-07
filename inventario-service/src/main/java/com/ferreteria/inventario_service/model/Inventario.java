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

    // El ID del inventario, autogenerado por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se guarda el ID del producto que viene del catalogo-service
    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId; 

    // El stock actual
    @Column(nullable = false)
    private Integer cantidad;
}