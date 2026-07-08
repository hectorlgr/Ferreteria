package com.ferreteria.inventario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {

    // ID del inventario, autogenerado por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guardar el ID del producto que viene del catalogo-service
    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId;

    // Stock actual
    @Column(nullable = false)
    private Integer cantidad;
}