package com.ferreteria.despacho_service.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DespachoRequestDto {

    @NotNull(message = "El ID de la venta es obligatorio")
    @Min(value = 1, message = "El ID de la venta debe ser un número positivo")
    private Long ventaId;

    @NotBlank(message = "La dirección de entrega no puede estar vacía")
    private String direccion;
}