package com.ferreteria.promocion_service.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromocionRequestDto {

    @NotBlank(message = "El código no puede estar vacío")
    private String codigo;

    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 99, message = "El descuento máximo es 99%")
    private Double porcentajeDescuento;

    @NotNull(message = "El estado (true/false) es obligatorio")
    private Boolean estado;
}