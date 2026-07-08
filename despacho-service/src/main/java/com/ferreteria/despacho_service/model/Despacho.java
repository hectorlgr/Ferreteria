package com.ferreteria.despacho_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "despachos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    @Column(nullable = false)
    private String direccion;

    // Estados logísticos internos: RECIBIDO_EN_BODEGA, PREPARANDO_PAQUETE, EN_RUTA,
    // ENTREGADO
    @Column(nullable = false)
    private String estado;
}