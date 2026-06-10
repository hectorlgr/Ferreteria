package com.ferreteria.despacho_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DespachoRequestDto {

    // ID de la venta asociada al despacho
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    // Dirección de entrega para el despacho
    @NotBlank(message = "La dirección de entrega no puede estar vacía")
    private String direccion;
}