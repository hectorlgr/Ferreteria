package com.ferreteria.catalogo_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(name = "ProductoRequest", description = "Modelo para la creación o actualización de un producto en el catálogo")
public class ProductoRequestDto {

    @Schema(description = "Nombre comercial del producto de ferretería", example = "Martillo de Uña 16oz", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Schema(description = "Descripción detallada de las especificaciones del artículo", example = "Martillo con mango de fibra de vidrio anti-vibración")
    private String descripcion;

    @Schema(description = "Marca del fabricante del producto", example = "Stanley")
    private String marca;

    @Schema(description = "Precio unitario neto del producto en pesos chilenos", example = "14990.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private Integer precio;
}