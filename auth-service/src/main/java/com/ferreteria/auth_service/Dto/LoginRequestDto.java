package com.ferreteria.auth_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @Schema(description = "Correo electrónico registrado", example = "cliente@correo.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Schema(description = "Contraseña secreta del usuario", example = "MiPassword123")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}