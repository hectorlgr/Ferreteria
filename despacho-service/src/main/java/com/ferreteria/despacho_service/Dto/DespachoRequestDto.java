package com.ferreteria.despacho_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "DespachoRequest", description = "Modelo de datos para solicitar la gestión de entrega (despacho) de un pedido físico")
public class DespachoRequestDto {

    @Schema(description = "ID único del pedido que requiere ser despachado", example = "1024", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long idPedido;

    @Schema(description = "ID del usuario comprador asociado al pedido", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @Schema(description = "Dirección física completa donde se entregará el paquete al cliente", example = "Av. Los Leones 456, Providencia", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La dirección de entrega no puede estar vacía")
    private String direccion;
}