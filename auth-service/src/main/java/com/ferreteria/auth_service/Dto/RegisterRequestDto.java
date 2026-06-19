package com.ferreteria.auth_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    @Schema(description = "Nombre completo o alias del nuevo usuario", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Correo electrónico válido para iniciar sesión", example = "juan.perez@correo.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Schema(description = "Contraseña segura", example = "MiPassword123")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}