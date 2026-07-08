package com.ferreteria.auth_service.controller;

import com.ferreteria.auth_service.Dto.LoginRequestDto;
import com.ferreteria.auth_service.Dto.RegisterRequestDto;
import com.ferreteria.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para inicio de sesión y registro de usuarios")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales del usuario y devuelve un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve el token."),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas (contraseña o email incorrecto).", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto dto) {
        // Si falla, el Service lanza UnauthorizedException y el GlobalExceptionHandler
        // devuelve el 401
        String token = userService.login(dto.getEmail(), dto.getPassword());

        Map<String, String> resp = new HashMap<>();
        resp.put("token", token);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Registrar un nuevo cliente", description = "Crea un usuario en el sistema con el rol predeterminado de CLIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error de validación o el email ya está registrado.", content = @Content)
    })
    @PostMapping("/register/cliente")
    public ResponseEntity<Map<String, String>> registerCliente(@Valid @RequestBody RegisterRequestDto dto) {
        // Si ya existe, el Service lanza BadRequestException y el
        // GlobalExceptionHandler devuelve el 400
        String resultado = userService.register(dto.getEmail(), dto.getPassword(), "CLIENTE", dto.getNombre());

        Map<String, String> resp = new HashMap<>();
        resp.put("message", resultado);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Operation(summary = "Registrar un administrador", description = "Crea un usuario en el sistema con privilegios elevados (rol ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador creado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Error de validación o el email ya está registrado.", content = @Content)
    })
    @PostMapping("/register/admin")
    public ResponseEntity<Map<String, String>> registerAdmin(@Valid @RequestBody RegisterRequestDto dto) {
        String resultado = userService.register(dto.getEmail(), dto.getPassword(), "ADMIN", dto.getNombre());

        Map<String, String> resp = new HashMap<>();
        resp.put("message", resultado);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}