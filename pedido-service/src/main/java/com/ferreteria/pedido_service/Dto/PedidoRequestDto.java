package com.ferreteria.pedido_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PedidoRequest", description = "Modelo de datos requerido para la generación de una nueva orden de pedido")
public class PedidoRequestDto {

    @Schema(description = "ID del usuario (cliente) que realiza la compra", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @Schema(description = "ID de la transacción de venta asociada a este pedido", example = "1024", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long idVenta;

    @Schema(description = "Dirección física completa para la entrega o facturación del pedido", example = "Av. Providencia 1234, Santiago", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
}