package com.ferreteria.resena_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ResenaRequest", description = "Modelo para la creación de una nueva reseña o valoración de un producto")
public class ResenaRequestDto {

    @Schema(description = "Identificador único del producto que se está valorando", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @Schema(description = "Identificador único del usuario que emite la reseña", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @Schema(description = "Calificación otorgada al producto (escala del 1 al 5)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @Schema(description = "Comentario opcional detallando la experiencia con el producto", example = "Excelente producto, muy duradero y cumple con las especificaciones.", maxLength = 500)
    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    private String comentario;
}