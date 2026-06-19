package com.ferreteria.promocion_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "PromocionRequest", description = "Modelo para la creación de un nuevo código de descuento en el sistema")
public class PromocionRequestDto {

    @Schema(description = "Código alfanumérico que el cliente ingresará para obtener el descuento", example = "VERANO2026", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El código no puede estar vacío")
    private String codigo;

    @Schema(description = "Porcentaje de descuento a aplicar sobre el total de la venta (valores entre 1 y 99)", example = "15.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El porcentaje es obligatorio")
    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 99, message = "El descuento máximo es 99%")
    private Double porcentajeDescuento;

    @Schema(description = "Indica si la promoción está activa y lista para usarse al momento de crearla", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El estado (true/false) es obligatorio")
    private Boolean estado;
}