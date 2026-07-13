package com.ferreteria.soporte_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "TicketRequest", description = "Modelo para la creación de un nuevo ticket de soporte y atención al cliente (Post-venta)")
public class TicketRequestDto {

    @Schema(description = "Identificador único del usuario que crea el ticket", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "Identificador único del pedido asociado al reclamo o consulta", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @Schema(description = "Categoría o tipificación del problema", example = "RETRASO_ENVIO", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 50, message = "La categoría no puede exceder los 50 caracteres")
    private String categoria;

    @Schema(description = "Título corto que resume el problema", example = "Mi pedido dice entregado pero no lo tengo", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 100, message = "El asunto no puede exceder los 100 caracteres")
    private String asunto;

    @Schema(description = "Detalle completo del problema escrito por el cliente", example = "Revisé en conserjería y me indican que no ha llegado ningún paquete a mi nombre.", maxLength = 500, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El mensaje detallado es obligatorio")
    @Size(max = 500, message = "El mensaje no puede exceder los 500 caracteres")
    private String mensaje;
}