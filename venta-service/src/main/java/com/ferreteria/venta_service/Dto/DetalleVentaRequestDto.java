package com.ferreteria.venta_service.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleVentaRequestDto {

    // Validaciones para el ID del producto, la cantidad y el precio unitario
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    // Cantidad debe ser al menos 1
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Precio unitario debe ser al menos 0
    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio unitario debe ser al menos 0")
    private Integer precioUnitario;
}