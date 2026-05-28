package com.ferreteria.catalogo_service.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoRequestDto {

    // Validaciones para asegurar que los datos recibidos sean correctos
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    // La descripción es obligatoria, pero no puede ser solo espacios en blanco
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    // La marca es obligatoria, pero no puede ser solo espacios en blanco
    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    // El precio es obligatorio y debe ser un número positivo
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precio;
}