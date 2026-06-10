package com.ferreteria.despacho_service.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DespachoRequestDto {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long idPedido;

    private Long idUsuario;

    private String direccion = "Dirección de usuario (Por definir)";
}