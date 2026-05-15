package com.ferreteria.venta_service.Dto; 

import lombok.Data;

@Data
public class UsuarioDto {
    private Long id; 
    private String nombre;
    private String email;
}