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

    // ID autogenerado por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de la venta asociada a este despacho, con este se hace el seguimiento (venta-service)
    @Column(name = "venta_id", nullable = false, unique = true)
    private Long ventaId;

    // Dirección de entrega para el despacho
    @Column(nullable = false)
    private String direccion;

    // Estado del despacho (Ej: "PREPARANDO", "EN_RUTA", "ENTREGADO")
    @Column(nullable = false)
    private String estado; 
}