package com.ferreteria.venta_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "DetalleVentaRequest", description = "Modelo que representa un ítem específico dentro del carrito de compras")
public class DetalleVentaRequestDto {

    @Schema(description = "ID del producto extraído del catálogo", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @Schema(description = "Cantidad de unidades a comprar de este producto", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto al momento de la venta", example = "14990", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio unitario debe ser al menos 0")
    private Integer precioUnitario;
}