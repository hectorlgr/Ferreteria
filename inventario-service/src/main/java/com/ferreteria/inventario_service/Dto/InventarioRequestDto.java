package com.ferreteria.inventario_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "InventarioRequest", description = "Modelo para la creación del registro de stock inicial de un producto")
public class InventarioRequestDto {

    @Schema(description = "Identificador único del producto proveniente del Catálogo", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del producto es obligatorio")
    @Min(value = 1, message = "El ID del producto debe ser mayor a 0")
    private Long productoId;

    @Schema(description = "Cantidad de unidades físicas disponibles en bodega", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer cantidad;
}