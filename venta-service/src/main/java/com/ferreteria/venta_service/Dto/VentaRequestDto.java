package com.ferreteria.venta_service.Dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaRequestDto {

    // Validaciones para el ID del usuario, el costo de despacho y la lista de detalles de venta
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    // Costo de despacho debe ser al menos 0
    @NotNull(message = "El costo de despacho es obligatorio")
    @Min(value = 0, message = "El costo de despacho debe ser al menos 0")
    private Integer costoDespacho;

    // Lista de detalles de venta no puede estar vacía
    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<DetalleVentaRequestDto> detalles;
}