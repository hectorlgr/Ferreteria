package com.ferreteria.despacho_service.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DespachoRequestDto {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotBlank(message = "La dirección de entrega no puede estar vacía")
    private String direccion;
}