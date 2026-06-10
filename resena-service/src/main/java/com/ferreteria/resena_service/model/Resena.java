package com.ferreteria.resena_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idProducto; // Referencia al catalogo-service

    @Column(nullable = false)
    private Long idUsuario; // Referencia al usuario-service

    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaResena;
}