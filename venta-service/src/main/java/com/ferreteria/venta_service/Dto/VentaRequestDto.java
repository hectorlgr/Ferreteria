package com.ferreteria.venta_service.Dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaRequestDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El costo de despacho es obligatorio")
    private Integer costoDespacho;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<DetalleVentaRequestDto> detalles;
}