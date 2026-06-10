package com.ferreteria.venta_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    // ID de la venta
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del usuario que realizó la venta (referencia a usuario-service)
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // Fecha y hora de la venta
    @Column(nullable = false)
    private LocalDateTime fechaVenta;

    // Total de la venta
    @Column(nullable = false)
    private Integer total;

    // Costo de despacho fijo incluido en la venta
    @Column(nullable = false)
    private Integer costoDespacho;

    // Relación 1 a Muchos
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetalleVenta> detalles;
}