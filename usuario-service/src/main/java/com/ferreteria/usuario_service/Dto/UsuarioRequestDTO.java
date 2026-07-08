package com.ferreteria.usuario_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "UsuarioRequest", description = "Modelo para la creación o actualización de los datos de un usuario")
public class UsuarioRequestDTO {

    @Schema(description = "Nombre completo o nombre de pila del usuario", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Correo electrónico del usuario. Servirá como identificador de acceso y debe ser único.", example = "juan.perez@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un formato de email válido")
    private String email;
}