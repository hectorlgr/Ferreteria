package com.ferreteria.venta_service.Dto; 

import lombok.Data;

@Data
public class UsuarioDto {

    // Campos para representar la información del usuario, como su ID, nombre y correo electrónico
    private Long id; 
    private String nombre;
    private String email;
}