package com.ferreteria.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ferreteria.auth_service.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint para login
    // http://localhost:9090/auth/login
    @PostMapping("/login")
    public java.util.Map<String, String> login(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String token = userService.login(email, password);

        java.util.Map<String, String> resp = new java.util.HashMap<>();
        if (token == null) {
            resp.put("status", "error");
            resp.put("token", "");
        } else {
            resp.put("status", "ok");
            resp.put("token", token);
        }
        return resp;
    }

    // Endpoint para registro de clientes
    // http://localhost:9090/auth/register/cliente
    @PostMapping("/register/cliente")
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> registerCliente(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String nombre = request.get("nombre");
        
        java.util.Map<String, String> resp = new java.util.HashMap<>();

        if (nombre == null || nombre.trim().isEmpty()) {
            resp.put("error", "El campo 'nombre' es obligatorio para registrar un cliente.");
            return org.springframework.http.ResponseEntity.badRequest().body(resp); 
        }

        String resultado = userService.register(email, password, "CLIENTE", nombre);

        resp.put("message", resultado);
        return org.springframework.http.ResponseEntity.ok(resp);
    }

    // Endpoint para registro de admins
    // http://localhost:9090/auth/register/admin
    @PostMapping("/register/admin")
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> registerAdmin(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String nombre = request.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            nombre = "Admin Ferretería";
        }
        String resultado = userService.register(email, password, "ADMIN", nombre);

        java.util.Map<String, String> resp = new java.util.HashMap<>();
        resp.put("message", resultado);
        return org.springframework.http.ResponseEntity.ok(resp);
    }
}