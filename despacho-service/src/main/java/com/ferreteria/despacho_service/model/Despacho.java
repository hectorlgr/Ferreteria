package com.ferreteria.despacho_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "despachos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación lógica con el microservicio de ventas
    @Column(name = "venta_id", nullable = false, unique = true)
    private Long ventaId;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String estado; // Ej: "PREPARANDO", "EN_RUTA", "ENTREGADO"

    // numero de seguimiento único para cada despacho
    @Column(name = "numero_seguimiento", nullable = false, unique = true)
    private Integer numeroSeguimiento; 
}