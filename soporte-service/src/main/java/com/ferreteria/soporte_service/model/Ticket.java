package com.ferreteria.soporte_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del usuario que crea el ticket
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // ID del pedido asociado
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    // Categoría del problema. Ej: "RECLAMO", "CONSULTA_GENERAL", "RETRASO_ENVIO"
    @Column(nullable = false, length = 50)
    private String categoria;

    // Un título corto. Ej: "No me llegó el comprobante al correo"
    @Column(nullable = false, length = 100)
    private String asunto;

    // El detalle completo del problema escrito por el cliente
    @Column(nullable = false, length = 500)
    private String mensaje;

    // Estado interno del ticket. Ej: "ABIERTO", "EN_REVISION", "RESUELTO"
    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}