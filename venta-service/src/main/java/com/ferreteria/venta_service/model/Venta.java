package com.ferreteria.venta_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; // Solo guardamos el ID, el nombre vive en usuario-service

    private LocalDateTime fecha;

    private Integer total;

    // Relación 1 a Muchos: Una venta tiene muchos detalles
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @JsonManagedReference // Evita un bucle infinito al devolver el JSON
    private List<DetalleVenta> detalles;
}