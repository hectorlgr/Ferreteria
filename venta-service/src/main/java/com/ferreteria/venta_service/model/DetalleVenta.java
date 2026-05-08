package com.ferreteria.venta_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    // ID del detalle de venta autogenerado por la db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Muchos a Uno: Muchos detalles pertenecen a una venta
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonBackReference
    private Venta venta;

    // ID del producto vendido que viene de producto-service
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    // Cantidad de productos vendidos
    @Column(nullable = false)
    private Integer cantidad;

    // Precio unitario del producto al momento de la venta
    @Column(nullable = false)
    private Integer precioUnitario;

    // Subtotal para este detalle aqui se guardara (cantidad * precioUnitario)
    @Column(nullable = false)
    private Integer subtotal;
}