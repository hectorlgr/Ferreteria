package com.ferreteria.venta_service.Dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "VentaRequest", description = "Estructura principal para procesar una nueva transacción de venta")
public class VentaRequestDto {

    @Schema(description = "ID del usuario que realiza la compra", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "Costo calculado para el envío a domicilio", example = "3500", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El costo de despacho es obligatorio")
    @Min(value = 0, message = "El costo de despacho debe ser al menos 0")
    private Integer costoDespacho;

    @Schema(description = "Dirección física para la entrega de los productos", example = "Av. Providencia 1234, Santiago", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La dirección de despacho es obligatoria")
    private String direccion;

    @Schema(description = "Código promocional opcional para aplicar descuentos", example = "VERANO2026")
    private String codigoPromocion;

    @Schema(description = "Lista de productos (ítems) incluidos en esta venta", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<DetalleVentaRequestDto> detalles;
}