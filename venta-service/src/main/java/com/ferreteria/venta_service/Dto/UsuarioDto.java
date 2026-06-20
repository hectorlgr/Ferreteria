package com.ferreteria.venta_service.Dto; 

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UsuarioTransfer", description = "Objeto de transferencia de datos con información básica del usuario")
public class UsuarioDto {

    @Schema(description = "ID interno del usuario", example = "5")
    private Long id; 
    
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;
    
    @Schema(description = "Correo electrónico de contacto", example = "juan.perez@email.com")
    private String email;
}