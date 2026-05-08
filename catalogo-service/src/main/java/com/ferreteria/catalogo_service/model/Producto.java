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

    //ID Generado automaticamente por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Nombre del producto Ej: "Taladro Percutor", "Martillo Carpintero"
    @Column(nullable = false)
    private String nombre;

    //Descripción del producto
    @Column(length = 200, nullable = false)
    private String descripcion;

    //Marca del producto Ej: "Makita", "Bosch", "Stanley"
    @Column(length = 100, nullable = false)
    private String marca;

    //Precio del producto Int
    @Column(nullable = false)
    private Integer precio;
}