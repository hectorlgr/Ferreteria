package com.ferreteria.usuario_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    // Validaciones para el nombre y el email
    @NotBlank(message = "El nombre es obligatorio") 
    private String nombre;
    
    // Validación para el email: no puede estar vacío y debe tener formato de email
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un formato de email válido")
    private String email;
}
